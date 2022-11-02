// import './App.css';
import {BrowserRouter, Routes, Route} from "react-router-dom";
import Login from "./Login/Login";
import Main from "./Main/Main";
import Search from "./Search/Search";
import Setting from "./Setting/Setting";
import NotFound from "./NotFound";

function App() {
    return (
        <div className={'App'}>
            <BrowserRouter>
                <Routes>
                    <Route path={"/"} element={<Login/>}/>
                    <Route path={"/Login"} element={<Login/>}/>
                    <Route path={"/Main"} element={<Main/>}/>
                    <Route path={"/Search"} element={<Search/>}/>
                    <Route path={"/Setting"} element={<Setting/>}/>
                    <Route path={"/*"} element={<NotFound/>}/>
                </Routes>
            </BrowserRouter>
        </div>
    );
}

export default App;
