import React, {useEffect, useState} from 'react';
import axios from 'axios';
import { Link } from "react-router-dom";
import { useMediaQuery } from 'react-responsive';
import './Main_Desktop.css';
import './Main_Mobile.css';
import MainProfile from './MainProfile';
import Groups from './Groups';
import Loading from "../Loading";

function Main() {
    const isMobile = useMediaQuery({ query: '(max-width: 930px'});
    const isDesktop = useMediaQuery({ query: '(min-width: 931px'});
    const [information, setInformation] = useState(null);
    useEffect(() => {
        axios.get('v1/member').then((response)=>{
            setInformation(response.data);
            console.debug(response.data);
        })
    }, []);

    function Common() {
        return (
            <div id="Wrapper">
                <Link to="/Search" state={information.memberInfo.id}>
                    <button id="Search"></button>
                </Link>
                <Link to="/Main">
                    <div id="Logo">
                        <img src="img/logo_simple.svg" alt="logo"></img>
                        {isMobile && <p>42서울 친구 자리 찾기 서비스</p>}
                    </div>
                </Link>
                <div id="MyProfile">
                    <MainProfile key={information.memberInfo.id} info={information.memberInfo} me={1}/>
                </div>
                <Groups groupInfo={information.groupInfo} friendInfo={information.groupFriendsList}/>
            </div>
        )
    }
    const MainContent=()=>{
        return (
            <>
                {isMobile &&  <div id="Mobile"><Common/></div>}
                {isDesktop && <div id="Desktop"><Common/></div>}
            </>
        )
    }
    //null대신 loading컴포넌트 넣기
    return (
        <div id="Main">
            {information != null ? <MainContent/> : <Loading/>}
        </div>
    )
}

export default Main;