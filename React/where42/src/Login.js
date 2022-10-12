import { useState } from 'react';
import { useMediaQuery } from 'react-responsive';
import './Login.css'
import './Login_Mobile.css'
import './Login_Tablet.css'
import './Login_Desktop.css'

function Login() {
    const isMobile = useMediaQuery({ query: '(max-width: 480px)'})
    const isTablet = useMediaQuery({ query: '(min-width: 481px) and (max-width: 1023px)'})
    const isDesktop = useMediaQuery({ query: '(min-width: 1024px)'})

    return (
        <div className="Login">
            {isMobile && <div id="Mobile"><Common/></div>}
            {isTablet && <div id="Tablet"><Common/></div>}
            {isDesktop && <div id="Desktop"><Common/></div>}
        </div>
    );
}
function Common() {
    const [modal, setModal] = useState(0);
    function clickDown() {
        const button = document.getElementById('Login-button');
        button.style = "background-image: url('img/login_button_click.svg'); background-size: contain";
    }
    function clickUp() {
        const button = document.getElementById('Login-button');
        button.style = "background-image: url('img/login_button.svg')";
        window.location.href = 'https://profile.intra.42.fr/users/sojoo';
    }
    function contactClick() {
        window.open('https://github.com/5ganization', '_blank');
    }
   
    function Modal() {
        return (
            <div id="Modal">
                <div id="ModalHeader"></div>
                <button id="ModalCancel" onClick={()=>{setModal(0)}}></button>
                <div id="ModalContent">내용을 쓰면 될것같아요.</div>
            </div>
        )
    }
    return (
        <div id="Common">
            {modal === 1 ? <Modal/> : null}
            <div id="Icon">
                <button id="contact" onClick={contactClick}></button>
                <button id="wiki" onClick={()=>{setModal(1)}} ></button>
            </div>
            <div id="Main">
                <div id="Logo">
                    <img src="img/logo.svg" alt="logo"></img>
                </div>
               <div id="Character">
                    <img src="img/character.svg" alt="character"></img>
                </div>
                <button id="Login-button" onMouseDown={clickDown} onMouseUp={clickUp}></button>
            </div>
        </div>
    )
}

export default Login;