import {useState} from "react";
import axios from "axios";
import Login from "./Login/Login";
import Loading from "./Etc/Loading";

function PublicRoute({ children }) {
    const [member, setMember] = useState(-1);
    axios.get("/v1/checkAgree")
        .then(() => {
            setMember(1);
        }).catch(() => {
            setMember(0);
        });
    console.log(member);
    return (
        <>
            {(member === -1) && <Loading/>}
            {(member === 1) && children}
            {(member === 0) && <Login/>}
        </>
    )
}

export default PublicRoute;