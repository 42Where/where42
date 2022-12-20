import {useNavigate} from "react-router";
import Loading from "./Loading";
import axios from 'axios';

function Oauth() {
    const nav = useNavigate();
    let code = new URL(window.location.href).searchParams.get("code");
    axios.post('/v1/auth/token', null, {params : {code: code}})
        .then((response) => {
            nav('/Main');
        }).catch((Error)=> {
            console.clear();
            if (Error.response.status === 401) {
                let data = Error.response.data.data;
                nav('/Agree', {state: data});
            }
            else if (Error.response.status === 409) {
                alert("정식 배포 기간이 아니므로 로그인 할 수 없습니다.");
                nav('/Login');
            }
            else {
                alert("다시 한 번 시도해 주세요.")
                nav('/Login');
            }
        });

    return (
        <Loading/>
    )
}

export default Oauth;