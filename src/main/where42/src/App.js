import {Route, Routes} from "react-router-dom";
import Home from "./Etc/Home";
import Login from "./Login/Login";
import Agree from "./Agree/Agree";
import Main from "./Main/Main";
import Search from "./Search/Search";
import Setting from "./Setting/Setting";
import NotFound from "./Etc/NotFound";
import Oauth from "./Etc/Oauth";
import Direct from "./Direct/Direct";
import PublicRoute from "./PublicRoute";
import PrivateRoute from "./PrivateRoute";
import RouteChangeTracker from "./RouteChangeTracker";

function App() {
    if (process.env.NODE_ENV === "production") {
        console.log = function no_console() {};
        console.warn = function no_console() {};
        console.error = function no_console() {};
        console.debug = function no_console() {};
    }

    RouteChangeTracker();

    return (
        <div className={'App'}>
            <Routes>
                <Route path={"/"} element={<Home/>}/>
                <Route path={"/Login"} element={<Login/>}/>
                <Route path={"/v2/auth/callback"} element={<Oauth/>}/>
                <Route path={"/*"} element={<NotFound/>}/>
                <Route path={"/Main"} element={<PrivateRoute><Main/></PrivateRoute>}/>
                <Route path={"/Search"} element={<PrivateRoute><Search/></PrivateRoute>}/>
                <Route path={"/Setting/*"} element={<PrivateRoute><Setting/></PrivateRoute>}/>
                <Route path={"/Agree"} element={<PublicRoute><Agree/></PublicRoute>}/>
                <Route path={"/Direct"} element={<Direct/>}/>
            </Routes>
        </div>
    );
}

export default App;
