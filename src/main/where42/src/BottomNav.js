import React from "react";
import {useState} from "react";
import './BottomNav.css';
import {Link} from "react-router-dom";
import {useLocation} from "react-router";

function BottomNav(){
    const location = useLocation();
    const splitUrl = location?.pathname?.split('/') ?? null;
    const page = splitUrl?.length > 1 ? splitUrl[splitUrl.length - 1] : null;
    const [activeNav] = useState(page === "Search" ? 1 : 2);

    return (
        <nav className={"BottomNavWrapper"}>
            <Link to={"/Search"} className={activeNav===1? "NavLink NavActive" : "NavLink"}>
                <div className={activeNav===1? "SearchIconActive" : "SearchIcon"}/>
                <span className={activeNav===1? "active" : "" }>친구 찾기</span>
            </Link>
            <Link to={"/Main"} className={activeNav===2? "NavLink NavActive" : "NavLink"}>
                <div className={activeNav===2? "HomeIconActive" : "HomeIcon"}/>
                <span className={activeNav===2? "active" : "" }>친구 목록</span>
            </Link>
            <Link to={"/Setting"} className={"NavLink"}>
                <div className={"SettingIcon"}/>
                <span>설정</span>
            </Link>
        </nav>
    );
};

export default BottomNav;