import React from 'react';
import {Link} from "react-router-dom";
import { useMediaQuery } from 'react-responsive';
import './Main.css';
import './Main_Desktop.css';
import './Main_Mobile.css';
import Profile from './Profile';
import Groups from './Groups';
// import test from './sample.json';
import axios from 'axios';

function Main() {
    let sample = test;
    // let sample ="";
    const isMobile = useMediaQuery({ query: '(max-width: 930px'});
    const isDesktop = useMediaQuery({ query: '(min-width: 931px'});
    axios.get('v1/member').then((response)=>{
        console.log(response.data);
        alert("axios success");
        // sample = response.data;
    }).catch((Error)=>{console.log("main error" + Error)})

    function Common() {
        return (
            <div id="Wrapper">
                <Link to="/Search" state={{name : sample.memeberInfo.name}}>
                    <button id="Search"></button>
                </Link>
                {/* <Link to="/Main"> */}
                <div id="Logo">
                    <img src="img/logo_simple.svg" alt="logo"></img>
                    {isMobile && <p>42서울 자리 찾기 서비스</p>}
                </div>
                {/* </Link> */}
                <div id="MyProfile">
                    <Profile key={sample.memeberInfo.id} info={sample.memeberInfo} me={1}/>
                </div>
                <Groups groupInfo={sample.groupInfo} friendInfo={sample.groupFriendInfo}/>
            </div>

        )
    }

    return (
        <div id="Main">
            {isMobile && <div id="Mobile"><Common/></div>}
            {isDesktop && <div id="Desktop"><Common/></div>}
        </div>
    )
}

export default Main;