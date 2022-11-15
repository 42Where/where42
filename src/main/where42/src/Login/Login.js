import { useState } from 'react';
import { useMediaQuery } from 'react-responsive';
import { Link } from 'react-router-dom';
import axios from 'axios'
import './Login.css'
import './Login_Mobile.css'
import './Login_Tablet.css'
import './Login_Desktop.css'
import './Login_Modal.css'

function Login() {
    const isMobile = useMediaQuery({ query: '(max-width: 480px)'});
    const isTablet = useMediaQuery({ query: '(min-width: 481px) and (max-width: 1023px)'});
    const isDesktop = useMediaQuery({ query: '(min-width: 1024px)'});
    const [modal, setModal] = useState(0);
    const xmlhttp = new XMLHttpRequest();
    let   content = null;

    xmlhttp.open("GET", "./wiki.txt", false);
    xmlhttp.overrideMimeType("text/html;charset=utf-8");
    xmlhttp.send();
    if (xmlhttp.status === 200) {
        content = xmlhttp.responseText;
    }

    function Common() {
        function clickDown() {
            const button = document.getElementById('LoginButton');
            button.style = "background-image: url('img/login_button_click.svg'); background-size: contain";
        }
        function clickUp() {
            const button = document.getElementById('LoginButton');
            button.style = "background-image: url('img/login_button.svg')"
            axios.get(  '/v1/login' ).then((response)=>{console.log(response.data)})
                .catch((Error)=>{console.log(Error)})
        }

        return (
            <div id="Common">
                <button id="Wiki" onClick={()=>{setModal(1)}} ></button>
                <div id="Logo">
                    <img src="img/logo.svg" alt="logo"></img>
                </div>
                <div id="Character">
                    <img src="img/character.svg" alt="character"></img>
                </div>
                <Link to="/Main">
                    <button id="LoginButton" onMouseDown={clickDown} onMouseUp={clickUp}></button>
                </Link>
            </div>
        )
    }
    
    function Modal() {
        return (
            <div id="Modal">
                <div id="ModalWrapper">
                    <div id="ModalHeader">
                        <span>어디있니?</span>
                    </div>
                    <div id="ModalContent">{content}</div>
                    <div id="ModalCancel">
                        <button id="CancelButton" onClick={()=>{setModal(0)}}>닫기</button>
                    </div>
                </div>
            </div>
        )
    }

    return (
        <div id="Login">
            {modal === 1 ? <Modal/> : null}
            {isMobile && <div id="Mobile"><Common/></div>}
            {isTablet && <div id="Tablet"><Common/></div>}
            {isDesktop && <div id="Desktop"><Common/></div>}
        </div>
    );
}

export default Login;