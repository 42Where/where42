import React, {useEffect, useState} from 'react';
import {instance} from "../AxiosApi";
import { Link } from "react-router-dom";
import { useMediaQuery } from 'react-responsive';
import './Main_Desktop.css';
import './Main_Mobile.css';
import MainProfile from './MainProfile';
import Groups from './Groups';
import Loading from "../Etc/Loading";
import * as Util from '../Util';
import BottomNav from "../BottomNav";

function Main() {
    const isMobile = useMediaQuery({ query: '(max-width: 930px)'});
    const isDesktop = useMediaQuery({ query: '(min-width: 931px)'});

    const [memberInfo, setMemberInfo] = useState(null);
    const [groupInfo, setGroupInfo] = useState(null);
    const [friendInfo, setFriendInfo] = useState(null);

    const [toggleFriend, setToggleFriend] = useState(0);
    const [toggleEval, setToggleEval] = useState(0);

    useEffect(() => {
        const getData = async () => {
            //memberInfo
            const mInfo = await instance.get('member/member');
            setMemberInfo(mInfo.data);
            //groupInfo
            await instance.get('member/group', {params : {id : mInfo.data.id}})
                .then((response)=>{
                setGroupInfo(response.data);
            })
            //groupFriendInfo
            await instance.get('member/friend', {params : {id : mInfo.data.id}})
                .then((response)=>{
                setFriendInfo(response.data);
            })
            setToggleEval(mInfo.data.eval);
        }
        getData();
    },[]);

    function ToggleFriendClick() {
        if (toggleFriend === 0)
            setToggleFriend(1);
        else
            setToggleFriend(0);
    }

    function ToggleEvalClick() {
        if (toggleEval === 0) {
            Util.ConfirmEval("설정 시 동료 평가 상태는 30분 동안만 유지됩니다.<br/>30분이 지나기 전에 동료 평가를 마쳤다면<br/>꼭 비활성화 해주세요!", "확인").then((res) => {
                if (res && (res.isConfirmed !== false)) {
                    document.getElementById("EvalButton").style.backgroundImage = "url(img/loadingdot2.gif)";
                    instance.post('member/evalon')
                        .then(() => {
                            memberInfo.eval = 1;
                            setToggleEval(1);
                        }).catch(() => {
                            Util.Alert("클러스터 외부에서는 설정이 불가능합니다.");
                            document.getElementById("EvalButton").style.backgroundImage = "url(img/detail_off.svg)";
                    });
                }
            });
        }
        else {
            Util.ConfirmEval("동료 평가를 마치시겠습니까?", "확인").then((res) => {
                if (res && (res.isConfirmed !== false)) {
                    instance.post('member/evaloff')
                        .then(() => {
                            memberInfo.eval = 0;
                            setToggleEval(0);
                        })
                }
            });
        }
    }

    function Common() {
        localStorage.setItem('name', memberInfo.name);
        return (
            <div id="Wrapper">
                <Link to="/Search">
                    <button id="Search"></button>
                </Link>
                <div id="Logo">
                    <Link to="/Main">
                        <img src="img/logo_simple.svg" alt="logo"></img>
                        {isMobile && <p>42서울 친구 자리 찾기 서비스</p>}
                    </Link>
                </div>
                <div id="MyProfile">
                    <MainProfile key={memberInfo.id} info={memberInfo} me={1}/>
                </div>
                <div id="Toggle">
                    <div id="ToggleFriend">
                        <span>출근한 친구만 보기</span>
                        <button className={"ToggleButton" + (toggleFriend ? " Active" : "")} onClick={() => ToggleFriendClick()}></button>
                    </div>
                    <div id="ToggleEval">
                        <span>동료 평가 중</span>
                        <button className={"ToggleButton" + (toggleEval ? " Active" : "")} id="EvalButton" onClick={() => ToggleEvalClick()}></button>
                    </div>
                </div>
                <div id="Groups">
                    <Groups groupInfo={groupInfo} friendInfo={friendInfo} toggle={toggleFriend}/>
                </div>
                <div id="Footer">
                    <div id="Link">
                        <a href='https://github.com/5ganization/where42' target="_blank" rel="noreferrer">Github</a>
                        <span>|</span>
                        <a href='https://www.notion.so/eff5de2f978a4164b52b68ad2ca2e05a' target="_blank" rel="noreferrer">어디 있니 설명서</a>
                        <span>|</span>
                        <a href='https://docs.google.com/forms/d/1mRxFHO4CA_X2WxtOR-BFjia1ctXOCys_v2IubjP7oUs/' target="_blank" rel="noreferrer">고객센터</a>
                    </div>
                    <div id="Version">
                        ⓒ 2022 어디 있니
                    </div>
                </div>
            </div>
        )
    }

    const MainContent=()=>{
        return (
            <>
                {isMobile &&
                    <div id="Mobile">
                        <Common/>
                        <BottomNav/>
                    </div>}
                {isDesktop && <div id="Desktop"><Common/></div>}
            </>
        )
    }
    return (
        <div id="Main">
            {memberInfo && groupInfo && friendInfo ? <MainContent/> : <Loading/>}
        </div>
    )
}

export default Main;