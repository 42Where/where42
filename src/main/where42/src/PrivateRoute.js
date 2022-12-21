import {useState} from "react";
import axios from "axios";
import Login from "./Login/Login";

function PrivateRoute({ children }) {
    const [login, setLogin] = useState(0);
    axios.get("/v1/home")
        .then(() => {
            setLogin(1);
        }).catch(() => {
            setLogin(0);
        });
    console.log(login);
    return login ? children : <Login/>;
}

export default PrivateRoute;