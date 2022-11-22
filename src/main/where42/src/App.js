// import './App.css';
import {Routes, Route} from "react-router-dom";
import Home from "./Home";
import Login from "./Login/Login";
import Agree from "./Agree/Agree";
import Main from "./Main/Main";
import Search from "./Search/Search";
import Setting from "./Setting/Setting";
import NotFound from "./NotFound";
import Oauth from "./Oauth";

function App() {
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
