import React, {useState} from 'react';
import { useMediaQuery } from 'react-responsive';
import { Link } from 'react-router-dom';
import Profile from './Profile';
import './Search_Desktop.css';
import './Search_Mobile.css';
import axios from "axios";
import {useNavigate} from "react-router";

function Search() {
    const isMobile = useMediaQuery({ query: '(max-width: 930px'});
    const isDesktop = useMediaQuery({ query: '(min-width: 931px'});
    const nav = useNavigate();

    const [information, setInformation] = useState([]);
    function SearchBox()
    {
        const [searchId, setSearch] = useState("");
        const searchChange=(e)=>{
            setSearch(e.target.value)
        }

        const SubmitId = (event) => {
            event.preventDefault();
            axios.get('v1/search', {params : {begin : searchId}}).then((response)=>{
                setInformation(response.data);
                // if (response.data === [])
                //     alert('검색 결과가 없습니다');
                // console.log(response.data);
            }).catch(()=>{nav('/Login')})
        }
        return (
            <div id="SearchWrapper">
                <div id="SearchBox">
                    <div id="SearchMascot">
                        <img src="img/character.svg" alt="character"></img>
                    </div>
                    <form onSubmit={SubmitId}>
                        <input type="text" placeholder="아이디를 입력해 주세요" value={searchId} onKeyPress={(e)=>{if (e.key==='Enter') SubmitId(e);}} onChange={searchChange}/>
                        <button id="SearchButton" type="submit"/>
                    </form>
                </div>
            </div>
        )
    }
    function SearchResults()
    {
        return (
            <div id="SearchResults">
                <div className="ProfileWrapper">
                    {information.map(person=>(
                        <Profile info={person}/>
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
                {information? <SearchResults/> : null}
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