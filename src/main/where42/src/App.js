import {Routes, Route} from "react-router-dom";
import Home from "./Etc/Home";
import Login from "./Login/Login";
import Agree from "./Agree/Agree";
import Main from "./Main/Main";
import Search from "./Search/Search";
import Setting from "./Setting/Setting";
import NotFound from "./Etc/NotFound";
import Oauth from "./Etc/Oauth";
import Admin from "./Admin";
import {useEffect, useState} from "react";
import {useLocation} from "react-router";
import {DecideRoute} from "./DecideRoute";

function App() {
    if (process.env.NODE_ENV === "production") {
        // console.log = function no_console() {};
        console.warn = function no_console() {};
        console.error = function no_console() {};
        console.debug = function no_console() {};
    }
    // const loc = useLocation();
    // const [pastLoc, setPastLoc] = useState("Home");
    // DecideRoute({current: loc.pathname, past: pastLoc});
    // useEffect(() => {
    //     setPastLoc(loc.pathname);
    // }, []);

    return (
        <div className={'App'}>
            <Routes>
                <Route path={"/"} element={<Home/>}/>
                <Route path={"/Login"} element={<Login/>}/>
                <Route path={"/Main"} element={<Main/>}/>
                <Route path={"/Search"} element={<Search/>}/>
                <Route path={"/Setting/*"} element={<Setting/>}/>
                <Route path={"/Agree"} element={<Agree/>}/>
                <Route path={"/Admin"} element={<Admin/>}/>
                <Route path={"/auth/login/callback"} element={<Oauth/>}/>
                <Route path={"/*"} element={<NotFound/>}/>
            </Routes>
        </div>
    );
}

export default App;
