import React, {useState} from 'react';
import { useMediaQuery } from 'react-responsive';
import { Link } from 'react-router-dom';
import SearchProfile from './SearchProfile';
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
        const alphaPattern = new RegExp('^[a-zA-Z]+$');
        const searchChange=(e)=>{
            const value = e.target.value;
            //마지막 char가 안지워짐 ㅜㅜ
            if (!value.match(alphaPattern))
            {
                e.target.value = searchId;
                return ;
            }
            setSearch(value)
            console.debug(e.target.value);
        }

        const SubmitId = (event) => {
            event.preventDefault();

            axios.get('v1/search', {params : {begin : searchId}}).then((response)=>{
                setInformation(response.data);
                //여기서 어떻게 data가 비어있는지 확인하는지 모르겠음 null, [null], [] 다안됨
                // if (response.data === [null])
                //     alert('검색 결과가 없습니다. 아이디를 확인해주세요');
                // console.log(response.data);
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
                        <input id="SearchInput" type="text" autoComplete={"off"} spellCheck={"false"} placeholder="아이디를 입력해 주세요" value={searchId} onKeyDown={searchKeyDown} onChange={searchChange}/>
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
                        <SearchProfile info={person} memberId={memberId}/>
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