import {useNavigate} from "react-router";
import Loading from "./Loading";
import axios from 'axios';
import * as Util from '../Util';
import {useEffect} from "react";

function Oauth() {
    const nav = useNavigate();
    let code = new URL(window.location.href).searchParams.get("code");
    useEffect(() => {
        axios.post('/v2/auth/token', {code: code})
            .then(() => {
                const directUrl = localStorage.getItem('direct');
                if (directUrl) {
                    localStorage.removeItem('direct');
                    window.location.href = directUrl;
                }
                else
                    nav('/Main');
            }).catch((Error)=> {
            console.clear();
            if (Error?.response?.status === 503)
                Util.Alert("오류가 발생했습니다. 현재 화면을 캡처해서 @sunghkim에게 슬랙 디앰을 보내주세요");
            else if (Error.response.status === 401) {
                let data = Error.response.data.data;
                nav('/Agree', {state: data});
            }
            else {
                Util.Alert("다시 한 번 시도해 주세요.")
                nav('/Login');
            }
        });
    },[]);

    return (
        <Loading/>
    )
}

export default Oauth;