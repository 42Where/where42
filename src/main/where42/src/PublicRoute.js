import {useEffect, useState} from "react";
import axios from "axios";
import Loading from "./Etc/Loading";
import NotFound from "./Etc/NotFound";

function PublicRoute({ children }) {
    const [member, setMember] = useState(-1);
    useEffect(() => {
        axios.get("/v2/checkAgree")
        .then(() => {
            setMember(1);
        }).catch(() => {
            setMember(0);
        });
    }, []);
    return (
        <>
            {(member === -1) && <Loading/>}
            {(member === 1) && children}
            {(member === 0) && <NotFound/>}
        </>
    )
}

export default PublicRoute;