import React from 'react';
import { useMediaQuery } from 'react-responsive';
import './Main.css';
import './Main_Desktop.css';
import './Main_Mobile.css';

function Main() {
    const isMobile = useMediaQuery({ query: '(max-width: 930px'});
    const isDesktop = useMediaQuery({ query: '(min-width: 931px'});

    function Common() {
        return (
            <div id="Wrapper">
                <button id="Burger">🔍</button>
                <div id="Logo">
                    <img src="img/logo_simple.svg" alt="logo"></img>
                    {isMobile && <p>42서울 자리 찾기 서비스</p>}
                </div>
                <div id="my-Profile">
                    <button id="Setting">⚙️</button>
                    <Profile/>
                </div>
                <div id="group-Name">즐겨찾기 (3)</div>
                <div id="profile-Wrapper">
                    <Profile/>
                    <Profile/>
                    <Profile/>
                </div>
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

function Profile() {
    return (
        <div id="Profile">
            <div id="Photo">
                <img src="img/erase.png" alt="user-face"></img>
            </div>
            <div id="Info">
                <div id="name-Circle">
                    <div id="Name">sojoo</div>
                    <div id="Circle"></div>
                </div>
                <div id="Locate">개포 1층 유튜브 스튜디오</div>
                <div id="Msg">안녕하세요!</div>
            </div>
        </div>
    )
}

export default Main;
