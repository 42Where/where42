import {useEffect, useState} from "react";
import axios from "axios";
import NotFound from "./Etc/NotFound";
import Loading from "./Etc/Loading";

function PrivateRoute({ children }) {
    const [login, setLogin] = useState(-1);
    useEffect(() => {
        axios.get("/v2/home")
        .then(() => {
            setLogin(1);
        }).catch(() => {
            setLogin(0);
        });
    }, []);
    return (
        <>
            {(login === -1) && <Loading/>}
            {(login === 1) && children}
            {(login === 0) && <NotFound/>}
        </>
    )
}

export default PrivateRoute;