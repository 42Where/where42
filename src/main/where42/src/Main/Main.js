import React, {useEffect, useState} from 'react';
import axios from 'axios';
import { Link } from "react-router-dom";
import { useMediaQuery } from 'react-responsive';
import './Main_Desktop.css';
import './Main_Mobile.css';
import Profile from './Profile';
import Groups from './Groups';
import Loading from "../Etc/Loading";

function Main() {
    const [information, setInformation] = useState(null);
    const isMobile = useMediaQuery({ query: '(max-width: 930px'});
    const isDesktop = useMediaQuery({ query: '(min-width: 931px'});
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
                    <Profile key={information.memberInfo.id} info={information.memberInfo} me={1}/>
                </div>
                <Groups groupInfo={information.groupInfo} friendInfo={information.groupFriendsList}/>
            </div>
        )
    }

    return (
        <div id="Main">
            {information != null ? isMobile && <div id="Mobile"><Common/></div> : <Loading/>}
            {information != null ? isDesktop && <div id="Desktop"><Common/></div> : <Loading/>}
        </div>
    )
}

export default Main;