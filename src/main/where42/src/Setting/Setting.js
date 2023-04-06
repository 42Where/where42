import { useEffect, useState } from 'react';
import { useMediaQuery } from 'react-responsive';
import { useNavigate } from 'react-router';
import { Routes, Route } from 'react-router-dom';
import { SettingFloor, SettingCluster, SettingSpot} from "./Setting_Locate";
import { SettingGnF, SettingFriend, SettingGroup } from "./Setting_Group_Friend";
import {instance} from "../AxiosApi";
import './Setting_Desktop.css';
import './Setting_Mobile.css';
import * as Util from '../Util';

function Setting() {
    const isMobile = useMediaQuery({ query: '(max-width: 930px)'});
    const isDesktop = useMediaQuery({ query: '(min-width: 931px)'});
    let size = "";
    if (isMobile)
        size = "Mobile";
    else if (isDesktop)
        size = "Desktop";
    const nav = useNavigate();
    const name = localStorage.getItem('name');
    useEffect(() => {
        localStorage.setItem('locate', JSON.stringify({
            planet: 0, floor: 0, cluster: 0
        }));
    }, []);

    function SettingChoice() {
        const [disButton, setDisBuutton] = useState(false);
        const SetLocateAlert = () => {
            setDisBuutton(true);
            instance.get('member/setting/locate')
            .then((res) => {
                const planet = res.data.data;
                if (res.data.data === 1) {
                    nav("/Setting/SetFloor", {state: {planet: planet}});
                } else if (res.data.data === 2) {
                    nav("/Setting/SetCluster", {state: {planet: planet}});
                }
                setDisBuutton(false);
            }).catch((error) => {
                if (error.response.status === 403) {
                    Util.Alert("클러스터 외부에 있으므로 수동 자리 정보를 등록할 수 없습니다.");
                } else if (error.response.status === 409) {
                    Util.Alert("자동 자리 정보가 존재하여 수동 자리 정보를 등록할 수 없습니다.");
                } else if (error.response.status === 503) {
                    Util.Alert("출퇴근 확인 중 일시적인 오류로 인하여 수동 자리 정보를 등록할 수 없습니다.");
                }
                setDisBuutton(false);
            });
        };
        const Logout = async () => {
            setDisBuutton(true);
            await instance.get('logout')
                .then(() => {
                    nav('/Login');
                    setDisBuutton(false);
                });
        }

        return (
            <div id="SettingChoice">
                <button id="Home" onClick={()=>{nav('/Main')}}></button>
                <div id="Comment">반가워요, {name}! 👋</div>
                <div id="BoxWrapper">
                    <button className='Box' disabled={disButton} onClick={() => {SetLocateAlert()}}>
                        <div className='BoxCap'>
                            {isMobile && <>수동 위치 설정</>}
                            {isDesktop && <>수동<br/>위치 설정</>}
                        </div>
                    </button>
                    <button className='Box' disabled={disButton} onClick={() => {nav('/Setting/SetMsg')}}>
                        <div className='BoxCap'>
                            {isMobile && <>상태 메시지 설정</>}
                            {isDesktop && <>상태 메시지<br/>설정</>}
                        </div>
                    </button>
                    <button className='Box' disabled={disButton} onClick={() => {nav('/Setting/SetGnF')}}>
                        <div className='BoxCap'>
                            {isMobile && <>그룹/친구 관리</>}
                            {isDesktop && <>그룹/친구<br/>관리</>}
                        </div>
                    </button>
                    <button className='Box' disabled={disButton} onClick={() => {Logout()}}>
                        <div className='BoxCap'>로그아웃</div>
                    </button>
                </div>
            </div>
        )
    }

    function SettingMsg() {
        const [msg, setMsg] = useState("");
        useEffect(() => {
            instance.get('member/setting/msg')
                .then((res) => {
                    setMsg(res.data);
                });
        }, []);
        const handleChange = ({target : {value}}) => setMsg(value);
        const handleSubmit = (event) => {
            event.preventDefault(); /*새로고침 방지*/
            instance.post('member/setting/msg', {msg})
                .then(() => {
                    Util.Alert("수정 완료!");
                    nav("/setting");
                });
        };

        return (
            <div id="SettingMsg">
                <button id="Back" onClick={()=>{nav('/Setting')}}></button>
                <button id="Home" onClick={()=>{nav('/Main')}}></button>
                <div id="Comment">상태 메시지를 입력해 주세요.</div>
                <div id="Comment2">상태 메시지는 최대 15자까지 입력 가능합니다.</div>
                <form onSubmit={handleSubmit}>
                    <input type="text" maxLength="15" placeholder={"상태 메시지를 입력해주세요."} spellCheck={false} value={msg} onChange={handleChange}/>
                    <button type="submit">확인</button>
                </form>
            </div>
        )
    }

    return (
        <div id="Setting">
            <Routes>
                <Route path={""} element={<div id={size}><SettingChoice/></div>}/>
                <Route path={"SetFloor"} element={<div id={size}><SettingFloor/></div>}/>
                <Route path={"SetCluster"} element={<div id={size}><SettingCluster/></div>}/>
                <Route path={"SetSpot"} element={<div id={size}><SettingSpot/></div>}/>
                <Route path={"SetMsg"} element={<div id={size}><SettingMsg/></div>}/>
                <Route path={"SetGnF"} element={<div id={size}><SettingGnF/></div>}/>
                <Route path={"SetGroup"} element={<div id={size}><SettingGroup/></div>}/>
                <Route path={"SetFriend"} element={<div id={size}><SettingFriend type="fDel"/></div>}/>
                <Route path={"SetGroupAdd"} element={<div id={size}><SettingFriend type="add"/></div>}/>
                <Route path={"SetGroupDel"} element={<div id={size}><SettingFriend type="del"/></div>}/>
            </Routes>
        </div>
    )
}

export default Setting;