import axios from "axios";
import {useNavigate} from "react-router";

function Oauth() {
    const nav = useNavigate();
    let code = new URL(window.location.href).searchParams.get("code");
    axios.get('/v1/auth/code', {params : {code: code}})
        .then((response) => {
            nav('/main');
        }).catch((Error)=> {
            let data = Error.response.data.data;
            nav('/agree', {state : data});
        });

    return (
        <div>
            기다려 주세용!
        </div>
    )
}

export default Oauth;