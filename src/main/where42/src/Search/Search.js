import React from 'react';
import { useState } from 'react';
import { useMediaQuery } from 'react-responsive';
import { Link } from 'react-router-dom';
//import { useLocation } from 'react-router-dom';
import Profile from './Profile';
import './Search_Desktop.css';
import './Search_Mobile.css';

import sample from './search.json';

function Search() {
    const isMobile = useMediaQuery({ query: '(max-width: 930px'});
    const isDesktop = useMediaQuery({ query: '(min-width: 931px'});

    //const location = useLocation();
    //const name = location.state?.name;
    //여기서 name을 api 요청시에 백엔드로 같이 보내줘야함
    //name 말고 id?
    //Search의 prop으로 id를 받을까?

    function SearchBox()
    {
        const [searchId, setSearch] = useState("");
        const searchChange=(e)=>{
            setSearch(e.target.value)
            // console.log(e.target.value);
        }
        //ente
        const SubmitId = (event) => {
            event.preventDefault();
            //api 호출 get memberId, searchId
            //respond json으로 profile 생성
            console.log(searchId);
        }
        return (
            <div id="SearchWrapper">
                <div id="SearchBox">
                    <div id="SearchMascot">
                        <img src="img/character.svg" alt="character"></img>
                    </div>
                    <form onSubmit={SubmitId}>
                        {/* input 필드의 height를 늘려도, font의 descender부분이 잘리는 증상있음 */}
                        <input type="text" placeholder="아이디를 입력해 주세요" value={searchId} onKeyPress={(e)=>{if (e.key==='Enter') SubmitId(e);}} onChange={searchChange}/>
                        <button id="SearchButton" type="submit"/>
                    </form>
                </div>
            </div>
        )
    }

    function Common() {
        return (
            <div id="Wrapper">
                <Link to="/Main">
                    <div id="Logo">
                        <img src="img/logo_simple.svg" alt="logo"></img>
                        {isMobile && <p>42서울 자리 찾기 서비스</p>}
                    </div>
                </Link>
                <SearchBox/>
                <div id="SearchResults">
                    {/* 검색 결과 */}
                    <div className="ProfileWrapper">
                        {/* {Profiles} */}
                        <Profile info={sample.matchUser[0]}/>
                        <Profile info={sample.matchUser[1]}/>
                        <Profile info={sample.matchUser[2]}/>
                    </div>
                </div>
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