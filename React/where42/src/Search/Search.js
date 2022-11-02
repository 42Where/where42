import React from 'react';
import { useMediaQuery } from 'react-responsive';
import { useLocation } from 'react-router-dom';
import Profile from './Profile';
import './Search_Desktop.css';
import './Search_Mobile.css';

import sample from './search.json';

function Search() {
    const isMobile = useMediaQuery({ query: '(max-width: 930px'});
    const isDesktop = useMediaQuery({ query: '(min-width: 931px'});

    const location = useLocation();
    const name = location.state?.name;
    //여기서 name을 api 요청시에 백엔드로 같이 보내줘야함
    
    function Common() {
        return (
            <div id="Wrapper">
                <div id="Logo">
                    {/* 로고 선택시 메인페이지 이동하도록 할건지? */}
                    <img src="img/logo_simple.svg" alt="logo"></img>
                    {isMobile && <p>42서울 자리 찾기 서비스</p>}
                </div>
                <div id="SearchBar">
                    <div id="SearchIcon"></div>
                    <div id="SearchContent"></div>
                </div>
                {/* enter 치면 state변경 가능? */}
                <div className="ProfileWrapper">
                    <Profile info={sample.matchUser[0]}/>
                    <Profile info={sample.matchUser[1]}/>
                    <Profile info={sample.matchUser[2]}/>

                </div>
                {/* 여기 검색 창 생성*/}
                {/* 검색 결과 */}
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