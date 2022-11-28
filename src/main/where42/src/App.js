// import './App.css';
import {Routes, Route} from "react-router-dom";
import Home from "./Etc/Home";
import Login from "./Login/Login";
import Agree from "./Agree/Agree";
import Main from "./Main/Main";
import Search from "./Search/Search";
import Setting from "./Setting/Setting";
import NotFound from "./Etc/NotFound";
import Oauth from "./Etc/Oauth";

function App() {
    if (process.env.NODE_ENV === "production") {
        console.log = function no_console() {};
        console.warn = function no_console() {};
        console.error = function no_console() {};
        console.clear = function no_console() {};
    }

    return (
        <div className={'App'}>
            <Routes>
                <Route path={"/"} element={<Home/>}/>
                <Route path={"/Login"} element={<Login/>}/>
                <Route path={"/Main"} element={<Main/>}/>
                <Route path={"/Search"} element={<Search/>}/>
                <Route path={"/Setting/*"} element={<Setting/>}/>
                <Route path={"/Agree"} element={<Agree/>}/>
                <Route path={"/*"} element={<NotFound/>}/>
                <Route path={"/auth/login/callback"} element={<Oauth/>}/>
            </Routes>
        </div>
    );
}

export default App;
