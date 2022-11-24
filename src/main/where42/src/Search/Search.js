import React, {useState} from 'react';
import { useMediaQuery } from 'react-responsive';
import { Link } from 'react-router-dom';
import Profile from './Profile';
import './Search_Desktop.css';
import './Search_Mobile.css';
import axios from "axios";
import {useLocation, useNavigate} from "react-router";

function Search() {
    const isMobile = useMediaQuery({ query: '(max-width: 930px'});
    const isDesktop = useMediaQuery({ query: '(min-width: 931px'});
    const location = useLocation();
    const nav = useNavigate();
    const [information, setInformation] = useState([]);
    const memberId = location.state;
    function SearchBox()
    {
        const [searchId, setSearch] = useState("");
        const searchChange=(e)=>{
            const value = e.target.value.replace(/[^a-zA-Z]/gi, '');
            setSearch(value)
        }

        const SubmitId = (event) => {
            event.preventDefault();
            axios.get('v1/search', {params : {begin : searchId}}).
            then((response)=>{
                setInformation(response.data);
                if (response.data.length === 0)
                    alert('검색 결과가 없습니다. 아이디를 확인해주세요');
            }).catch(()=>{nav('/Login')})
        }
        const searchKeyDown = (event) =>{
            let charCode = event.keyCode;
            if (charCode === 'Enter')
                SubmitId(event);
        }

        return (
            <div id="SearchWrapper">
                <div id="SearchBox">
                    <div id="SearchMascot">
                        <img src="img/character.svg" alt="character"></img>
                    </div>
                    <form onSubmit={SubmitId}>
                        <input id="SearchInput" type="text"
                               maxlength='10' autoComplete={"off"}
                               spellCheck={"false"} placeholder="아이디를 입력해 주세요"
                               value={searchId}
                               onKeyDown={searchKeyDown}
                               onChange={searchChange} autoFocus/>
                        <button id="SearchButton" type="submit"/>
                    </form>
                </div>
            </div>
        )
    }

    function SearchResults(props)
    {
        const memberId = props.memberId;
        return (
            <div id="SearchResults">
                <div className="ProfileWrapper">
                    {information.map(person=>(
                        <Profile info={person} memberId={memberId}/>
                    ))}
                </div>
            </div>
        )
    }

    function Common() {
        return (
            <div id="Wrapper">
                <div id="Logo">
                    <Link to="/Main">
                        <img src="img/logo_simple.svg" alt="logo"></img>
                    </Link>
                    {isMobile && <p>42서울 친구 자리 찾기 서비스</p>}
                </div>
                <SearchBox/>
                {information? <SearchResults memberId={memberId}/> : null}
            </div>
        )
    }

    return (
        <div id="Search">
            {isMobile && <div id="Mobile"><Common/></div>}
            {isDesktop && <div id="Desktop"><Common/></div>}
        </div>
    )
}

export default Search;