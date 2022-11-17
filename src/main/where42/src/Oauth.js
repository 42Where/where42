import react from 'react';
import {useLocation} from "react-router";
import {parsePath} from "react-router-dom";
import * as queryString from "querystring";

function Oauth(){
    const locate = useLocation();
    const path = locate.pathname;
    const code = queryString.parse(path);
    console.log("code " + code);

    axios.get('/v1/auth/callback', {param : {"code":code} }).then(response){
        console.log(response.data);
        //response에 따라 띄울 컴포넌트 결정하기
    }
}
