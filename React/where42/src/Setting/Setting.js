import { useState } from 'react';
import { useMediaQuery } from 'react-responsive';
import { useLocation } from 'react-router';
import { Link } from 'react-router-dom';
import { Routes, Route } from 'react-router-dom';
import './Setting_Desktop.css';
import './Setting_Mobile.css';

function Setting() {
    const location = useLocation();
    const name = location.state?.name;
    const isMobile = useMediaQuery({ query: '(max-width: 930px'});
    const isDesktop = useMediaQuery({ query: '(min-width: 931px'});
    let [choice, setChoice] = useState(0);
    let [locate, setLocate] = useState({
        planet: 0, floor: 0, cluster: 0, spot: 0
    })
    console.log(choice);
    console.log(locate);

    function SettingChoice() {
        return (
            <div id="SettingChoice">
                <div id="Comment">반가워요, hyunjcho{name}! 👋</div>
                <div id="BoxWrapper">
                    <div className='Box' onClick={()=>{setChoice(1)}}>
                        <div className='BoxCap'>
                            {isMobile && <>수동 위치 설정</>}
                            {isDesktop && <>수동<br/>위치 설정</>}
                        </div>
                    </div>
                    <div className='Box' onClick={()=>{setChoice(2)}}>
                        <div className='BoxCap'>
                            {isMobile && <>상태 메시지 설정</>}
                            {isDesktop && <>상태 메시지<br/>설정</>}
                        </div>
                    </div>
                    <Box cap="그룹 설정" choice={3}/>
                    <Link to="/Login">
                        <Box cap="로그아웃" choice={0}/>
                    </Link>
                </div>
            </div>
        )
    }
    
    function SettingMsg() {
        /*처음에 user의 msg 가져와서 초기화에 넣기*/
        const [msg, setMsg] = useState("안녕하세요");
        const handleChange = ({target : {value}}) => setMsg(value);
        const handleSubmit = (event) => {
            event.preventDefault(); /*새로고침 방지*/
            alert(JSON.stringify(msg, null, 1).replace(/"/gi, ""));
            /*변경된 msg를 백으로 넘겨주는 api 호출*/
            setChoice(0);
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

    /*자리설정 컴포넌트들은 따로 js로 모아서 뺄까?*/
    /*box를 컴포넌트로 만들어서 재사용할까..?*/
    /*자리설정 박스 선택시 alert 나오고 (모달로?) 넘어가기*/
    function SettingPlanet() {
        return (
            <div id="SettingPlanet">
                <div id="Comment">클러스터 선택</div>
                <div id="BoxWrapper">
                    <Box cap="개포" planet={1} choice={4}/>
                    <Box cap="서초" planet={2} choice={5}/>
                </div>
            </div>
        )
    }

    /*Floor 박스 배치 어떻게가 좋을지?*/
    function SettingFloor() {
        return (
            <div id="SettingFloor">
                <div id="Comment">층 수 선택</div>
                <div id="BoxWrapper">
                    <Box cap="1층" floor={1} choice={61}/>
                    <Box cap="2층" floor={2} choice={62}/>
                    <Box cap="3층" floor={3} choice={63}/>
                    <Box cap="4층" floor={4} choice={64}/>
                    <Box cap="5층" floor={5} choice={65}/>
                    <Box cap="B1/옥상" floor={6} choice={66}/>
                </div>
            </div>
        )
    }

    function SettingCluster() {
        return (
            <div id="SettingCluster">
                <div id="Comment">클러스터 선택</div>
                <div id="BoxWrapper">
                    <Box cap="7 클러스터" cluster={7} choice={71}/>
                    <Box cap="8 클러스터" cluster={8} choice={72}/>
                    <Box cap="9 클러스터" cluster={9} choice={73}/>
                    <Box cap="10 클러스터" cluster={10} choice={74}/>
                </div>
            </div>
        )
    }

    function SettingSpot() {
        /*spot만 따로 분기를 나눌까? Box따로 빼서?*/
    }

    function SettingGroup() {
        /*그룹 설정 화면 상세 구성이 필요해요~*/
        return (
            <div id="SettingGroup">
                <div id="Comment">그룹 설정</div>
            </div>
        )
    }

    /*로그아웃은 따로 기능 구현 없이 Login 화면으로 넘어가도록 하면 될듯?
    42gg도 그러하더랍니다*/

    /*매개변수를 문자열 하나만 전달 할건데 그것도 꼭 props로 전달해줘야 하나요?*/
    function Box(props) {
        if (props.planet) {
            return (
                <div className='Box' onClick={() => {
                    setLocate((prev) => {
                        return {...prev, planet: props.planet}
                    });
                    setChoice(props.choice);
                }}>
                    <div className='BoxCap'>{props.cap}</div>
                </div>
            )
        }
        else if (props.floor >= -1) {
            return (
                <div className='Box' onClick={() => {
                    setLocate((prev) => {
                        return {...prev, floor: props.floor}
                    });
                    setChoice(props.choice);
                }}>
                    <div className='BoxCap'>{props.cap}</div>
                </div>
            )
        }
        else if (props.cluster) {
            return (
                <div className='Box' onClick={() => {
                    setLocate((prev) => {
                        return {...prev, cluster: props.cluster}
                    });
                    setChoice(props.choice);
                }}>
                    <div className='BoxCap'>{props.cap}</div>
                </div>
            )
        }
        else if (props.choice >= 0) {
            return (
                <div className='Box' onClick={()=>{setChoice(props.choice)}}>
                    <div className='BoxCap'>{props.cap}</div>
                </div>
            )
        }
    }

    /*return 구역 (실제로 화면에 띄울 컴포넌트 선택)*/
    if (choice === 0) {
        return (
            <div id="Setting">
                <Routes>
                    {isDesktop && <Route path={"SetMsg"} element={<div id="Desktop"><SettingMsg/></div>}/>}
                    {isMobile && <Route path={""} element={<div id="Mobile"><SettingChoice/></div>}/>}
                    {isDesktop && <Route path={""} element={<div id="Desktop"><SettingChoice/></div>}/>}
                </Routes>
            </div>
        )
    }
    else if (choice === 1) {
        return (
            <div id="Setting">
                {isMobile && <div id="Mobile"><SettingPlanet/></div>}
                {isDesktop && <div id="Desktop"><SettingPlanet/></div>}
            </div>
        )
    }
    else if (choice === 2) {
        return (
            <div id="Setting">
                {isMobile && <div id="Mobile"><SettingMsg/></div>}
                {isDesktop && <div id="Desktop"><SettingMsg/></div>}
            </div>
        )
    }
    else if (choice === 3) {
        return (
            <div id="Setting">
                {isMobile && <div id="Mobile"><SettingGroup/></div>}
                {isDesktop && <div id="Desktop"><SettingGroup/></div>}
            </div>
        )
    }
    else if (choice === 4) {
        return (
            <div id="Setting">
                {isMobile && <div id="Mobile"><SettingFloor/></div>}
                {isDesktop && <div id="Desktop"><SettingFloor/></div>}
            </div>
        )
    }
    else if (choice === 5) {
        return (
            <div id="Setting">
                {isMobile && <div id="Mobile"><SettingCluster/></div>}
                {isDesktop && <div id="Desktop"><SettingCluster/></div>}
            </div>
        )
    }
}

export default Setting;

/*근데... 상황에 따라 다른 컴포넌트로 띄우기(SPA)로 하면 뒤로가기를 못하는데
== 뒤로 가기 누르면 아예 이전 페이지로 넘어감
사용자 입장에서 불편하진 않을까요?*/