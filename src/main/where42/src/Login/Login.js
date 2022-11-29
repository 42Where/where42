import { useState} from 'react';
import { useMediaQuery } from 'react-responsive';
import { useNavigate } from "react-router";
import instance from "../AxiosApi";
import axios from "axios";
import './Login.css'
import './Login_Mobile.css'
import './Login_Tablet.css'
import './Login_Desktop.css'
import './Login_Modal.css'

function Login() {
    const isMobile = useMediaQuery({ query: '(max-width: 480px)'});
    const isTablet = useMediaQuery({ query: '(min-width: 481px) and (max-width: 1023px)'});
    const isDesktop = useMediaQuery({ query: '(min-width: 1024px)'});
    const [modal, setModal] = useState(false);
    const xmlhttp = new XMLHttpRequest();
    let   content = null;
    const nav = useNavigate();

    xmlhttp.open("GET", "./wiki.txt", false);
    xmlhttp.overrideMimeType("text/html;charset=utf-8");
    xmlhttp.send();
    if (xmlhttp.status === 200) {
        content = xmlhttp.responseText;
    }

    function Common() {
        let serverurl = "";
        instance.get('auth/login')
            .then((res) => {
                serverurl = res.data;
            });
        function clickDown() {
            const button = document.getElementById('LoginButton');
            button.style = "background-image: url('img/login_button_click.svg'); background-size: contain";
        }

        function clickUp() {
            const button = document.getElementById('LoginButton');
            button.style = "background-image: url('img/login_button.svg')";
            axios.get(  '/v1/login' )
                .then((res) => {
                    nav('/Main');
                }).catch((Error) => {
                    const errData = Error.response.data;
                    console.clear();
                    if ('data' in errData)
                        nav('/Agree', {state : errData.data});
                    else
                        window.location.href = serverurl;
                });
        }

        return (
            <div id="Common">
                <button id="Wiki" onClick={()=>{setModal(true)}} ></button>
                <div id="Logo">
                    <img src="img/logo.svg" alt="logo"></img>
                </div>
                <div id="Character">
                    <img src="img/character.svg" alt="character"></img>
                </div>
                <button id="LoginButton" onMouseDown={clickDown} onMouseUp={clickUp}></button>
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
                        <button id="CancelButton" onClick={()=>{setModal(false)}}>닫기</button>
                    </div>
                </div>
            </div>
        )
    }

    return (
        <div id="Login">
            {modal === true ? <Modal/> : null}
            {isMobile && <div id="Mobile"><Common/></div>}
            {isTablet && <div id="Tablet"><Common/></div>}
            {isDesktop && <div id="Desktop"><Common/></div>}
        </div>
    );
}

export default Login;