import React, {useState} from 'react';
import { useMediaQuery } from 'react-responsive';
import { Link } from 'react-router-dom';
import SearchProfile from './SearchProfile';
import './Search.css';
import './Search_Desktop.css';
import './Search_Mobile.css';
import instance from "../AxiosApi";
import {useLocation} from "react-router";
import egg from './egg.json'

function Search() {
    const isMobile = useMediaQuery({ query: '(max-width: 930px)'});
    const isDesktop = useMediaQuery({ query: '(min-width: 931px)'});
    const location = useLocation();
    const [information, setInformation] = useState([]);
    const [loading, setLoading] = useState(false);
    const memberId = location.state;

    function SearchBox()
    {
        const [searchId, setSearch] = useState("");
        const SubmitId = (event) => {
            if (searchId === "" || loading === true)
            {
                event.preventDefault();
                return ;
            }
            if (searchId === "where42")
            {
                event.preventDefault();
                setInformation(egg);
                return ;
            }
            setLoading(true);
            if (searchId === "어디있니")
            {
                instance.get('search/where42')
                    .then((response)=>{
                    setInformation(response.data);
                    setLoading(false);
                })
                alert("어디있니는 당신의 친구랍니다 :)");
                return ;
            }
            instance.get('search', {params : {begin : searchId}})
                .then((response)=>{
                if (response.data.length === 0)
                    alert('검색 결과가 없습니다. 아이디를 확인해주세요');
                console.log(response.data);
                setLoading(false);
                setInformation(response.data);
            })
        }

        const searchChange=(e)=>{
            setSearch(e.target.value);
        }

        const searchKeyDown = (event) =>{
            let charCode = event.keyCode;
            if (charCode === 13)
                SubmitId(event);
        }

        return (
            <div id="SearchWrapper">
                <div id="SearchBox">
                    <div id="SearchMascot">
                        <img src="img/character.svg" alt="character"></img>
                    </div>
                    <form>
                        <input id="SearchInput" type="text"
                               maxlength='10' autoComplete={"off"}
                               spellCheck={"false"} placeholder="아이디를 입력해 주세요"
                               value={searchId}
                               onKeyDown={searchKeyDown}
                               onChange={searchChange} autoFocus/>
                        <button id="SearchButton" type="submit" onClick={SubmitId}/>
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

    function Common() {
        return (
            <div id="Wrapper">
                <div id="Logo">
                    <Link to="/Main">
                        <img src="img/logo_simple.svg" alt="logo"></img>
                    </Link>
                    {isMobile && <p>42서울 친구 자리 찾기 서비스</p>}
                </div>
                <SearchBox/>
                {loading? <img id="LoadingImg" src={"img/spinner.gif"} alt="로딩중"/> : null}
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
