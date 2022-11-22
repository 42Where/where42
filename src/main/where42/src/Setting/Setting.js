import { useEffect, useRef, useState } from 'react';
import { useMediaQuery } from 'react-responsive';
import { useLocation, useNavigate } from 'react-router';
import { Routes, Route } from 'react-router-dom';
import { Link } from 'react-router-dom';
import axios from 'axios';
import './Setting_Desktop.css';
import './Setting_Mobile.css';
import spot from './spot.json';

function Setting() {
    const location = useLocation();
    const name = location.state?.name;
    if (name) {
        localStorage.setItem('userName', name);
    }
    const nav = useNavigate();
    const isMobile = useMediaQuery({ query: '(max-width: 930px'});
    const isDesktop = useMediaQuery({ query: '(min-width: 931px'});

    /*새로고침 시에도 유지될 수 있도록 localstorage에 저장*/
    let [locate, setLocate] = useState(
        () => JSON.parse(window.localStorage.getItem("locate")) ||
            { planet: 0, floor: 0, cluster: 0, spot: 0});

    useEffect(() => {
        window.localStorage.setItem("locate", JSON.stringify(locate));
        console.log(locate);
    }, [locate]);

    function SettingChoice() {
        const name = localStorage.getItem('userName');
        const SetLocateAlert = () => {
            axios.get('/v1/member/setting/locate')
                .then((res) => {
                    console.log(res.data);
                    setLocate((prev) => {
                        return {...prev, planet: res.data.data}
                    });
                    nav("/Setting/SetPlanet");
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
        const [msg, setMsg] = useState("");
        useEffect(() => {
            axios.get('/v1/member/setting/msg')
                .then((res) => {
                    setMsg(res.data.msg);
                }).catch(() => {
                    nav("/Login");
                });
        }, []);
        /*렌더링 후 한번만 호출하기 위해 useeffect 사용, 근데 setState는 다시 렌더링을 하게 만드므로 두번째 매개변수의 의존성을 null로 두면 관찰할 요소가
        없기 때문에 한번만 실행됨*/
        const handleChange = ({target : {value}}) => setMsg(value);
        const handleSubmit = (event) => {
            event.preventDefault(); /*새로고침 방지*/
            axios.post('/v1/member/setting/msg', {msg: msg})
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

    function SettingGnF() {
        return (
            <div id="SettingGnF">
                <div id="Comment">그룹/친구 관리</div>
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
        let arr = [];
        axios.get('/v1/groupFriend')
            .then((res) => {
                console.log(res);
                arr = res.data;
            }).catch(() => {
                nav("/Login");
        });
        /*친구가 없어서... 확인이... 안도ㅐ...*/
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
            if (delList.size > 0 && window.confirm("정말 삭제하시겠습니까?")) {
                /*삭제 부탁하는 api 호출*/
                alert("삭제 완료!");
                nav("/Setting");
            }
        }
        return (
            <div id="SettingFriend">
                <div id="Comment">현재 친구 목록</div>
                <div id="Comment2">삭제할 친구를 선택한 후 '삭제' 버튼을 눌러주세요.</div>
                <form onSubmit={handleSubmit}>
                    <div id="MemberWrapper">
                        {
                            arr.map((value, index) => (
                                <MemberList user={value} addList={addList} key={index}/>
                            ))
                        }
                    </div>
                    <button type="submit">삭제</button>
                </form>
            </div>
        )
    }

    function SettingGroup() {
        const [arr, setArr] = useState(null);
        useEffect(() => {
            axios.get('/v1/group')
                .then((res) => {
                    setArr(res.data);
                    console.log(res.data);
                }).catch(() => {
                    nav("/Login");
                });
        }, []);
        const [name, setName] = useState("");
        const handleChange = ({target : {value}}) => setName(value);
        const handleSubmit = async (e) => {
            e.preventDefault();
            if (name === "즐겨찾기" || name === "기본" || name === "친구 목록") {
                alert("사용할 수 없는 그룹명입니다.");
            } else {
                try {
                    await axios.post('/v1/group', null, {params: {groupName: name}});
                    alert("그룹을 생성하였습니다.");
                    nav('/Setting/SetGroup');
                } catch (err) {
                    if (err.response.status === 401) {
                        nav('/Login');
                    } else if (err.response.status === 409) {
                        alert("이미 존재하는 그룹명입니다.");
                        nav('/Setting/SetGroup');
                    }
                }
            }
        }
        console.log(arr);

        return (
            <div id="SettingGroup">
                <div id="Comment">그룹 관리</div>
                <form onSubmit={handleSubmit}>
                    <input type="text" maxLength="10" placeholder="그룹명은 10자까지 입력 가능합니다." spellcheck="false" value={name} onChange={handleChange}/>
                    <button type="submit">추가</button>
                </form>
                <div id="GroupList">
                    {
                        arr && arr.map((group, index) => {
                            return <GroupList name={group.groupName} id={group.groupId} key={index}/>
                        })
                    }
                </div>
            </div>
        )
    }

    function SettingGroupAdd() {
        const loc = useLocation();
        const groupInfo = loc.state;
        /*그룹 ID로 친구 string arr 얻는 api 호출*/
        const arr = ["sojoo", "minkkang", "heeskim", "donghyuk", "seokchoi", "minseunk", "hyeondle"]; /*샘플*/
        /*아래는 친구 삭제와 동일한 함수들 (재사용할 방법은?)*/
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
            if (delList.size > 0 && window.confirm("추가 하시겠습니까?")) {
                /*추가 부탁하는 api 호출*/
                alert("추가 완료!");
                nav("/Setting/SetGroup");
            }
        }

        return (
            <div id="SettingGroupAdd">
                <div id="Comment">{groupInfo.groupName}</div>
                <div id="Comment2">그룹에 추가할 친구를 선택해 주세요.</div>
                <form onSubmit={handleSubmit}>
                    <div id="MemberWrapper">
                        {
                            arr.map((value, index) => (
                                <MemberList user={value} addList={addList} key={index}/>
                            ))
                        }
                    </div>
                    <button type="submit">추가</button>
                </form>
            </div>
        )
    }

    function SettingGroupDel() {
        const loc = useLocation();
        const groupInfo = loc.state;
        /*그룹 ID로 친구 string arr 얻는 api 호출*/
        const arr = ["sojoo", "minkkang", "heeskim", "donghyuk", "seokchoi", "minseunk", "hyeondle"]; /*샘플*/
        /*아래는 친구 삭제와 동일한 함수들 (재사용할 방법은?)*/
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
            if (delList.size > 0 && window.confirm("정말 삭제 하시겠습니까?")) {
                /*삭제 부탁하는 api 호출*/
                alert("삭제 완료!");
                nav("/Setting/SetGroup");
            }
        }

        return (
            /*id Del로 수정(일단은 css 때문에)*/
            <div id="SettingGroupAdd">
                <div id="Comment">{groupInfo.groupName}</div>
                <div id="Comment2">그룹에서 삭제할 친구를 선택해 주세요.</div>
                <form onSubmit={handleSubmit}>
                    <div id="MemberWrapper">
                        {
                            arr.map((value, index) => (
                                <MemberList user={value} addList={addList} key={index}/>
                            ))
                        }
                    </div>
                    <button type="submit">삭제</button>
                </form>
            </div>
        )
    }

    function GroupList(props) {
        const inputRef = useRef(null);
        const [name, setName] = useState(props.name);
        const delGroup = () => {
            if (window.confirm("정말 삭제하시겠습니까?")) {
                axios.delete('/v1/group/' + props.id)
                    .then(() => {
                        alert("'" + props.name + "' 그룹을 삭제하였습니다.");
                        nav("/setting/SetGroup");
                    });
            }
        }
        const modGroup = () => {
            if (inputRef.current.disabled === true) {
                inputRef.current.disabled = false;
                inputRef.current.focus();
            }
            else {
                inputRef.current.disabled = true;
                if (name === "즐겨찾기" || name === "기본" || name === "친구 목록") {
                    alert("사용할 수 없는 그룹명입니다.");
                    nav('/Setting/SetGroup');
                } else {
                    axios.post('/v1/group/' + props.id, null, {params: {changeName: name}})
                        .then(() => {
                            nav('/Setting/SetGroup');
                        }).catch(() => {
                            alert("중복된 이름의 그룹이 존재합니다.");
                            nav('/Setting/SetGroup');
                        });
                }
            }
        }
        const handleChange = ({target : {value}}) => setName(value);

        return (
            <div className='Group'>
                <input type="text" maxLength="10" value={name} spellcheck="false" onChange={handleChange} ref={inputRef} disabled/>
                <div className='GroupButtons'>
                    <button onClick={modGroup}></button>
                    <button onClick={delGroup}></button>
                </div>
                <div className='FriendButtons'>
                    <Link to="/Setting/SetGroupAdd" state={{id: props.id, groupName: props.name}}>
                        <button></button>
                    </Link>
                    <Link to="/Setting/SetGroupDel" state={{id: props.id, groupName: props.name}}>
                        <button></button>
                    </Link>
                </div>
            </div>
        )
    }

    function MemberList(props) {
        const [checked, setChecked] = useState(false);
        useEffect(() => {props.addList(props.user, checked)}, [props, checked]);

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
                <div className='Box' onClick={(e) => {
                    if (props.floor === 3) {
                        e.preventDefault();
                        alert("현재 3층은 공사중이므로 선택할 수 없습니다.");
                    }
                    else {
                        setLocate((prev) => {
                            return {...prev, cluster: 0, floor: props.floor}
                        })
                        nav("SetSpot");
                    }
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
                    axios.post('/v1/member/setting/locate', {locate})
                        .then(() => {
                            alert("수정 완료!");
                            nav("/Setting");
                        }).catch(() => {
                            nav("/Login");
                    });
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
                {isMobile && <Route path={"SetGroup"} element={<div id="Mobile"><SettingGroup/></div>}/>}
                {isDesktop && <Route path={"SetGroup"} element={<div id="Desktop"><SettingGroup/></div>}/>}
                {isMobile && <Route path={"SetFriend"} element={<div id="Mobile"><SettingFriend/></div>}/>}
                {isDesktop && <Route path={"SetFriend"} element={<div id="Desktop"><SettingFriend/></div>}/>}
                {isMobile && <Route path={"SetGroupAdd"} element={<div id="Mobile"><SettingGroupAdd/></div>}/>}
                {isDesktop && <Route path={"SetGroupAdd"} element={<div id="Desktop"><SettingGroupAdd/></div>}/>}
                {isMobile && <Route path={"SetGroupDel"} element={<div id="Mobile"><SettingGroupDel/></div>}/>}
                {isDesktop && <Route path={"SetGroupDel"} element={<div id="Desktop"><SettingGroupDel/></div>}/>}
            </Routes>
        </div>
    )
}

export default Setting;