import { useEffect, useState } from 'react';
import { useMediaQuery } from 'react-responsive';
import { useLocation, useNavigate } from 'react-router';
import { Link } from 'react-router-dom';
import { Routes, Route } from 'react-router-dom';
import './Setting_Desktop.css';
import './Setting_Mobile.css';
import spot from './spot.json';

function Setting() {
    const location = useLocation();
    const name = location.state?.name;
    const nav = useNavigate();
    const isMobile = useMediaQuery({ query: '(max-width: 930px'});
    const isDesktop = useMediaQuery({ query: '(min-width: 931px'});
    
    /*새로고침 시에도 유지될 수 있도록 localstorage에 저장*/
    let [locate, setLocate] = useState(
        () => JSON.parse(window.localStorage.getItem("locate")) ||
        { planet: 0, floor: 0, cluster: 0, spot: 0});

    useEffect(() => {
        window.localStorage.setItem("locate", JSON.stringify(locate));
    }, [locate]);
    console.log(locate);

    function SettingChoice() {
        const SetLocateAlert = () => {
            /*api 호출해서 200 받으면 (출근 && 자동 자리 정보 없음) nav("SetPlanet"),
            아니면 alert 후 nav("/Setting")*/
            alert("자동 자리 정보가 존재하여 수동 자리 정보를 등록할 수 없습니다.");
            nav("SetPlanet");
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
                    <Link to="/Login">
                        <div className='Box'>
                            <div className='BoxCap'>로그아웃</div>
                        </div>
                    </Link>
                </div>
            </div>
        )
    }

    /*자리설정 컴포넌트들은 따로 js로 모아서 뺄까?*/
    function SettingPlanet() {
        return (
            <div id="SettingPlanet">
                <div id="Comment">클러스터 선택</div>
                <div id="BoxWrapper">
                    <Link to="/Setting/SetFloor">
                        <Box cap="개포" planet={1}/>
                    </Link>
                    <Link to="/Setting/SetCluster">
                        <Box cap="서초" planet={2}/>
                    </Link>
                </div>
            </div>
        )
    }

    function SettingFloor() {
        return (
            <div id="SettingFloor">
                <div id="Comment">층 수 선택</div>
                <div id="BoxWrapper">
                    <Box cap="1층" floor={1}/>
                    <Box cap="2층" floor={2}/>
                    <Box cap="3층" floor={3}/>
                    <Box cap="4층" floor={4}/>
                    <Box cap="5층" floor={5}/>
                    <Box cap="B1/옥상" floor={6}/>
                </div>
            </div>
        )
    }

    function SettingCluster() {
        return (
            <div id="SettingCluster">
                <div id="Comment">클러스터 선택</div>
                <div id="BoxWrapper">
                    <Box cap="7 클러스터" cluster={7}/>
                    <Box cap="8 클러스터" cluster={8}/>
                    <Box cap="9 클러스터" cluster={9}/>
                    <Box cap="10 클러스터" cluster={10}/>
                </div>
            </div>
        )
    }

    function SettingSpot() {
        let spotNum;

        if (locate.floor) {
            spotNum = locate.floor;
        }
        else if (locate.cluster) {
            spotNum = locate.cluster;
        }

        return (
            <div id="SettingSpot">
                <div id="Comment">장소 선택</div>
                <div id="BoxWrapper">
                    {
                        spot[spotNum].map((value, index) => (
                            <Box cap={value} key={index}/>
                        ))
                    }
                </div>
            </div>
        )
    }

    function SettingMsg() {
        /*처음에 user의 msg 가져와서 초기화에 넣기*/
        /*null 이면 뒤에 흐린 글씨 넣기*/
        const [msg, setMsg] = useState("안녕하세요");
        const handleChange = ({target : {value}}) => setMsg(value);
        const handleSubmit = (event) => {
            event.preventDefault(); /*새로고침 방지*/
            alert(JSON.stringify(msg, null, 1).replace(/"/gi, ""));
            /*변경된 msg를 백으로 넘겨주는 api 호출*/
            nav("/setting");
        }

        return (
            <div id="SettingMsg">
                <div id="Comment">상태 메시지를 입력해 주세요.</div>
                <div id="Comment2">상태 메시지는 최대 15자까지 입력 가능합니다.</div>
                <form onSubmit={handleSubmit}>
                    <input type="text" maxLength="15" value={msg} onChange={handleChange}></input>
                    <button type="submit">확인</button>
                </form>
            </div>
        )
    }

    function SettingGnF() {
        return (
            <div id="SettingGroup">
                <div id="Comment">그룹 설정</div>
                <div id="BoxWrapper">
                    <Link to="/Setting/SetGroup">
                        <div className='Box'>
                            <div className='BoxCap'>그룹 관리</div>
                        </div>
                    </Link>
                    <Link to="/Setting/SetFriend">
                        <div className='Box'>
                            <div className='BoxCap'>친구 삭제</div>
                        </div>
                    </Link>
                </div>
            </div>
        )
    }

    function SettingFriend() {
        /*친꾸 목록 불러오는 api 호출*/
        const arr = ["sojoo", "hyunjcho", "heeskim"]; /*샘플*/
        const [delList, setDelList] = useState(new Set());
        const addList = (user, checked) => {
            if (checked) {
                delList.add(user);
                setDelList(delList);
            }
            else if (!checked && delList.has(user)) {
                delList.delete(user);
                setDelList(delList);
            }
        }
        const handleSubmit = (event) => {
            event.preventDefault();
            console.log(delList);
        }
        return (
            <div id="SettingFriend">
                <div id="Comment">현재 친구 목록</div>
                <div id="Comment2">삭제할 친구를 선택한 후 '삭제' 버튼을 눌러주세요.</div>
                <form onSubmit={handleSubmit}>
                    {
                        arr.map((value, index) => (
                            <MemberList user={value} addList={addList} key={index}/>
                        ))
                    }
                    <button type="submit">삭제</button>
                </form>
            </div>
        )
    }

    function MemberList(props) {
        const [checked, setChecked] = useState(false);
        useEffect(() => {props.addList(props.user, checked)}, [checked]);

        return (
            <div className='User'>
                <label><input type="checkbox" onChange={()=>{setChecked(!checked)}}/>{props.user}</label>
            </div>
        )
    }

    function Box(props) {
        if (props.planet) {
            return (
                <div className='Box' onClick={() => {
                    setLocate((prev) => {
                        return {...prev, planet: props.planet}
                    });
                }}>
                    <div className='BoxCap'>{props.cap}</div>
                </div>
            )
        }
        else if (props.floor) {
            return (
                <div className='Box' onClick={() => {
                    setLocate((prev) => {
                        return {...prev, cluster: 0, floor: props.floor}
                    })
                    nav("SetSpot");
                }}>
                    <div className='BoxCap'>{props.cap}</div>
                </div>
            )
        }
        else if (props.cluster) {
            return (
                <div className='Box' onClick={() => {
                    setLocate((prev) => {
                        return {...prev, floor: 0, cluster: props.cluster}
                    })
                    nav("SetSpot");
                }}>
                    <div className='BoxCap'>{props.cap}</div>
                </div>
            )
        }
        else {
            return (
                <div className='Box' onClick={() => {
                    setLocate((prev) => {
                        return {...prev, spot: props.cap}
                    })
                    /*저장 api 호출*/
                    alert("수정 완료!");
                    nav("/Setting");
                }}>
                    <div className='BoxCap'>{props.cap}</div>
                </div>
            )
        }
    }

    return (
        <div id="Setting">
            <Routes>
                {isMobile && <Route path={""} element={<div id="Mobile"><SettingChoice/></div>}/>}
                {isDesktop && <Route path={""} element={<div id="Desktop"><SettingChoice/></div>}/>}
                {isMobile && <Route path={"SetPlanet"} element={<div id="Mobile"><SettingPlanet/></div>}/>}
                {isDesktop && <Route path={"SetPlanet"} element={<div id="Desktop"><SettingPlanet/></div>}/>}
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
                {/*isMobile && <Route path={"SetGroup"} element={<div id="Mobile"><SettingGroup/></div>}/>}
                {isDesktop && <Route path={"SetGroup"} element={<div id="Desktop"><SettingGroup/></div>}/>*/}
                {isMobile && <Route path={"SetFriend"} element={<div id="Mobile"><SettingFriend/></div>}/>}
                {isDesktop && <Route path={"SetFriend"} element={<div id="Desktop"><SettingFriend/></div>}/>}
                {/*isMobile && <Route path={"SetGroupAdd"} element={<div id="Mobile"><SettingGroupAdd/></div>}/>}
                {isDesktop && <Route path={"SetGroupAdd"} element={<div id="Desktop"><SettingGroupAdd/></div>}/>}
                {isMobile && <Route path={"SetGroupDel"} element={<div id="Mobile"><SettingGroupDel/></div>}/>}
                {isDesktop && <Route path={"SetGroupDel"} element={<div id="Desktop"><SettingGroupDel/></div>}/>} */}
            </Routes>
        </div>
    )
}

export default Setting;