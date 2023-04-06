import React, {useState} from "react";
import {Link} from "react-router-dom";
import {useMediaQuery} from 'react-responsive';
import {useNavigate} from "react-router";
import {instance} from "../AxiosApi";
import * as Util from "../Util";

const MainProfile = (props) => {
    const isDesktop = useMediaQuery({ query: '(min-width: 931px)'});
    
    const info = props.info;
    if (info.img === null || info.img === "null")
        info.img = 'img/character.svg';
    const locate = CombineLocate(info.locate, info.inOrOut, info.eval);

    const nav = useNavigate();
    //settinglocatealert와 동일한 함수 util로 사용할 방법? (refactor)
    const [disableDiv, setDisable] = useState(false);
    const locateClick=()=> {
        if (props.me && disableDiv === true)
            return (false);
        if (props.me && info.inOrOut === 4)
            Util.Alert("출퇴근 정보 오류가 발생하여 수동자리설정이 불가능합니다. 상태메시지를 이용해주세요.");
        setDisable(true);
        if (props.me) {
            instance.get('member/setting/locate')
                .then((res) => {
                    const planet = res.data.data;
                    if (res.data.data === 1) {
                        nav("/Setting/SetFloor", {state: {planet: planet}});
                    } else if (res.data.data === 2) {
                        nav("/Setting/SetCluster", {state: {planet: planet}});
                    }
                    setDisable(false);
                }).catch((error) => {
                if (error.response.status === 403) {
                    Util.Alert("클러스터 외부에 있으므로 수동 자리 정보를 등록할 수 없습니다.");
                } else if (error.response.status === 409) {
                    Util.Alert("자동 자리 정보가 존재하여 수동 자리 정보를 등록할 수 없습니다.");
                }
                setDisable(false);
            });
        }
    };

    return (
        <div className="Profile">
            <div className={"Photo" + (info.inOrOut === 4 ? " Error" : "")}>
                 <img src={info.img} alt="user-face" onClick={()=>window.open("https://profile.intra.42.fr/users/" + info.name, '_blank')}/>
            </div>
            <div className="Info">
                <div className="Name">{info.name}</div>
                <div className="LocateWrapper">
                    <div className={info.inOrOut === 4? "ErrLocate" : "ErrLocate hidden"}>[출/퇴근 미확인]&nbsp;</div>
                    <div className="Locate" disabled={disableDiv} onClick={locateClick}>{locate}</div>
                </div>
                <div className="Msg">{info.msg}</div>
            </div>
            {
                isDesktop && props.me ?
                <div id="SettingWrapper">
                    <Link to="/Setting">
                        <button id="Setting"></button>
                    </Link>
                </div> : null
            }
        </div>
    );
};

export function CombineLocate(locate, inOutState, evalOn) {
    let position = "";
    if (inOutState === 2)
        position = "자리 정보 없음";
    else if (inOutState === 3)
        position = "외출";
    else if (inOutState === 4 && locate.planet === 4)
            position += "자리 정보 없음";
    else if (inOutState === 0)
        position = "퇴근";
    else
    {
        //inoutstate가 1이고 planet이 0인 경우 없음? : 확인 필요
        if (locate.planet === 1)
            position += "개포 ";
        else if (locate.planet === 2)
            position += "서초 ";
        if (evalOn === 1)
            position += "동료 평가 중";
        else {
            if ((locate.planet === 1 && locate.floor === 0) || (locate.planet === 2 && locate.cluster === 0))
                position += "클러스터 내 ";
            else if (locate.floor === 6) {
                if (locate.spot === "오픈스튜디오")
                    position += "지하 1층 ";
                else
                    position += "옥상 "
            }
            else if (locate.floor > 0 && locate.floor < 6)
                position += locate.floor.toString() + "층 ";
            else if (locate.cluster >= 7 && locate.cluster <= 10)
                position += locate.cluster.toString() + "클 ";

            if (locate.spot !== null)
                position += locate.spot;
        }
    }
    return position;
}

export default MainProfile;
