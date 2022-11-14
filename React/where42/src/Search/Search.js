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

    function SearchBox()
    {
        const [searchId, setSearch] = useState("");
        // const searchChange = ({target : {value}}) => setSearch(value);
        const searchChange=(e)=>{
            //검색창에 한글자 입력할때마다 포커스가 풀리는 증상 있음 
            //하나의 컴포넌트안에 넣어서 재렌더링이 되기 때문
            // form부분을 컴포넌트 분리해서 해결
            // e.preventDefault();
            setSearch(e.target.value)
            console.log(e.target.value);
        }
        const SubmitId = (event) => {
            event.preventDefault();
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
                        <input type="text" placeholder="아이디를 입력해 주세요" value={searchId} onChange={searchChange}/>
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
                {/* enter 칠때 state변경 가능? */}
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