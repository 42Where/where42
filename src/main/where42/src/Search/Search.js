import React, {useState, useRef, useEffect} from 'react';
import { useMediaQuery } from 'react-responsive';
import { Link } from 'react-router-dom';
import SearchProfile from './SearchProfile';
import './Search.css';
import './Search_Desktop.css';
import './Search_Mobile.css';
import instance from "../AxiosApi";
import {useLocation, useNavigate} from "react-router";
import Loading from "../Etc/Loading";

function Search() {
    const isMobile = useMediaQuery({ query: '(max-width: 930px'});
    const isDesktop = useMediaQuery({ query: '(min-width: 931px'});
    const location = useLocation();
    const nav = useNavigate();
    const [information, setInformation] = useState([]);
    const [loading, setLoading] = useState(false);
    const memberId = location.state;

    function setLoadingParent(loading){
        setLoading(loading);
    }

    function SearchBox({loading, setLoading})
    {
        const inputRef = useRef(null);
        const [searchId, setSearch] = useState("");

        useEffect(()=>{
            inputRef.current.disabled = false;
        }, []);

        const SubmitId = (event) => {
            if (searchId === "" || inputRef.current.disabled === true)
                return ;
            event.preventDefault();
            inputRef.current.disabled = true;
            setLoading(true);
            instance.get('search', {params : {begin : searchId}}).then((response)=>{
                if (response.data.length === 0)
                    alert('검색 결과가 없습니다. 아이디를 확인해주세요');
                setLoading(false);
                setInformation(response.data);
            }).catch((Error)=>{
                // console.log(Error);
                nav('/Login')
            })
            inputRef.current.disabled = false;
        }

        const searchChange=(e)=>{
            const value = e.target.value.replace(/[^a-zA-Z2-9]/gi, '');
            setSearch(value);
        }

        const searchKeyDown = (event) =>{
            let charCode = event.keyCode;
            if (charCode === 'Enter')
                SubmitId(event);
        }

        return (
            <div id="SearchWrapper">
                <div id="SearchBox">
                    <div id="SearchMascot">
                        <img src="img/character.svg" alt="character"></img>
                    </div>
                    <form onSubmit={SubmitId}>
                        <input id="SearchInput" type="text"
                               maxlength='10' autoComplete={"off"}
                               spellCheck={"false"} placeholder="아이디를 입력해 주세요"
                               value={searchId}
                               onKeyDown={searchKeyDown}
                               onChange={searchChange} autoFocus/>
                        <button id="SearchButton" type="submit" ref={inputRef} disabled/>
                    </form>
                </div>
            </div>
        )
    }

    function SearchResults(props)
    {
        const memberId = props.memberId;
        return (
            <div id="SearchResults">
                <div className="ProfileWrapper">
                    {information.map(person=>(
                        <SearchProfile info={person} memberId={memberId}/>
                    ))}
                </div>
            </div>
        )
    }
    //
    // function SearchLoading(){
    //     return (
    //         <div id={"LoadingWrapper"}>
    //             <div id={"LoadingContent"}>로딩 스피너</div>
    //             <div id={"LoadingCharacter"}>캐릭터</div>
    //         </div>
    //     )
    // }

    function Common() {
        return (
            <div id="Wrapper">
                <div id="Logo">
                    <Link to="/Main">
                        <img src="img/logo_simple.svg" alt="logo"></img>
                    </Link>
                    {isMobile && <p>42서울 친구 자리 찾기 서비스</p>}
                </div>
                <SearchBox loading={loading} setLoading={setLoadingParent}/>
                {loading? <Loading/> : null}
                {/* 데스크탑 버전에서는 땀방울이 안보임 ㅜㅜ*/}
                {loading === false && information? <SearchResults memberId={memberId}/> : null}
            </div>
        )
    }

    return (
        <div id="Search">
            {isMobile && <div id="Mobile"><Common/></div>}
            {isDesktop && <div id="Desktop"><Common/></div>}
        </div>
    )
}

export default Search;
