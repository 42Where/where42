import { useEffect, useState } from 'react';
import { useMediaQuery } from 'react-responsive';
import { useLocation, useNavigate } from 'react-router';
import { Routes, Route } from 'react-router-dom';
import { Link } from 'react-router-dom';
import axios from 'axios';
import { SettingFloor, SettingCluster, SettingSpot} from "./Setting_Locate";
import { SettingGnF, SettingFriend, SettingGroup } from "./Setting_Group_Friend";
import './Setting_Desktop.css';
import './Setting_Mobile.css';

function Setting() {
    const isMobile = useMediaQuery({ query: '(max-width: 930px'});
    const isDesktop = useMediaQuery({ query: '(min-width: 931px'});
    const nav = useNavigate();
    const location = useLocation();
    let name = location.state?.name;
    if (name)
        localStorage.setItem('userName', name);
    else
        name = localStorage.getItem('userName');
    useEffect(() => {
        localStorage.setItem('locate', JSON.stringify({
            planet: 0, floor: 0, cluster: 0
        }));
    }, []);

    function SettingChoice() {
        const SetLocateAlert = () => {
            axios.get('/v1/member/setting/locate')
                .then((res) => {
                    const planet = res.data.data;
                    if (res.data.data === 1) {
                        nav("/Setting/SetFloor", {state: {planet: planet}});
                    } else if (res.data.data === 2) {
                        nav("/Setting/SetCluster", {state: {planet: planet}});
                    }
                }).catch((error) => {
                if (error.response.status === 401) {
                    nav("/Login");
                } else if (error.response.status === 403) {
                    alert("클러스터 외부에 있으므로 수동 자리 정보를 등록할 수 없습니다.");
                } else if (error.response.status === 409) {
                    alert("자동 자리 정보가 존재하여 수동 자리 정보를 등록할 수 없습니다.");
                }
            });
        };
        const Logout = () => {
            axios.get('/v1/logout')
                .then(() => {
                    nav('/Login');
                });
        }

        return (
            <div id="SettingChoice">
                <div id="Comment">반가워요, {name}! 👋</div>
                <div id="BoxWrapper">
                    <div className='Box' onClick={() => {SetLocateAlert()}}>
                        <div className='BoxCap'>
                            {isMobile && <>수동 위치 설정</>}
                            {isDesktop && <>수동<br/>위치 설정</>}
                        </div>
                    </div>
                    <Link to="SetMsg">
                        <div className='Box'>
                            <div className='BoxCap'>
                                {isMobile && <>상태 메시지 설정</>}
                                {isDesktop && <>상태 메시지<br/>설정</>}
                            </div>
                        </div>
                    </Link>
                    <Link to="SetGnF">
                        <div className='Box'>
                            <div className='BoxCap'>
                                {isMobile && <>그룹/친구 관리</>}
                                {isDesktop && <>그룹/친구<br/>관리</>}
                            </div>
                        </div>
                    </Link>
                    <div className='Box' onClick={() => {Logout()}}>
                        <div className='BoxCap'>로그아웃</div>
                    </div>
                </div>
            </div>
        )
    }

    function SettingMsg() {
        const [msg, setMsg] = useState("");
        useEffect(() => {
            axios.get('/v1/member/setting/msg')
                .then((res) => {
                    setMsg(res.data.msg);
                }).catch(() => {
                    nav("/Login");
                });
        }, []);
        const handleChange = ({target : {value}}) => setMsg(value);
        const handleSubmit = (event) => {
            event.preventDefault(); /*새로고침 방지*/
            axios.post('/v1/member/setting/msg', {msg})
                .then(() => {
                    alert("수정 완료!");
                    nav("/setting");
                }).catch(() => {
                    nav("/Login");
            });
        };

        return (
            <div id="SettingMsg">
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
                {isMobile && <Route path={""} element={<div id="Mobile"><SettingChoice/></div>}/>}
                {isDesktop && <Route path={""} element={<div id="Desktop"><SettingChoice/></div>}/>}
                {isMobile && <Route path={"SetFloor"} element={<div id="Mobile"><SettingFloor/></div>}/>}
                {isDesktop && <Route path={"SetFloor"} element={<div id="Desktop"><SettingFloor/></div>}/>}
                {isMobile && <Route path={"SetCluster"} element={<div id="Mobile"><SettingCluster/></div>}/>}
                {isDesktop && <Route path={"SetCluster"} element={<div id="Desktop"><SettingCluster/></div>}/>}
                {isMobile && <Route path={"SetSpot"} element={<div id="Mobile"><SettingSpot/></div>}/>}
                {isDesktop && <Route path={"SetSpot"} element={<div id="Desktop"><SettingSpot/></div>}/>}
                {isMobile && <Route path={"SetMsg"} element={<div id="Mobile"><SettingMsg/></div>}/>}
                {isDesktop && <Route path={"SetMsg"} element={<div id="Desktop"><SettingMsg/></div>}/>}
                {isMobile && <Route path={"SetGnF"} element={<div id="Mobile"><SettingGnF/></div>}/>}
                {isDesktop && <Route path={"SetGnF"} element={<div id="Desktop"><SettingGnF/></div>}/>}
                {isMobile && <Route path={"SetGroup"} element={<div id="Mobile"><SettingGroup/></div>}/>}
                {isDesktop && <Route path={"SetGroup"} element={<div id="Desktop"><SettingGroup/></div>}/>}
                {isMobile && <Route path={"SetFriend"} element={<div id="Mobile"><SettingFriend type="fDel"/></div>}/>}
                {isDesktop && <Route path={"SetFriend"} element={<div id="Desktop"><SettingFriend type="fDel"/></div>}/>}
                {isMobile && <Route path={"SetGroupAdd"} element={<div id="Mobile"><SettingFriend type="add"/></div>}/>}
                {isDesktop && <Route path={"SetGroupAdd"} element={<div id="Desktop"><SettingFriend type="add"/></div>}/>}
                {isMobile && <Route path={"SetGroupDel"} element={<div id="Mobile"><SettingFriend type="del"/></div>}/>}
                {isDesktop && <Route path={"SetGroupDel"} element={<div id="Desktop"><SettingFriend type="del"/></div>}/>}
            </Routes>
        </div>
    )
}

export default Setting;