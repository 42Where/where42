import {useNavigate} from "react-router";
import Loading from "./Loading";
import axios from 'axios';

function Oauth() {
    const nav = useNavigate();
    let code = new URL(window.location.href).searchParams.get("code");
    axios.get('/v1/auth/code', {params : {code: code}})
        .then((response) => {
            nav('/main');
        }).catch((Error)=> {
            console.log(Error);
            let data = Error.response.data.data;
            nav('/agree', {state : data});
        });

    return (
        <Loading/>
    )
}

export default Oauth;