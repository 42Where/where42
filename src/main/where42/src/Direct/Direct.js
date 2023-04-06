import { useNavigate } from "react-router";
import { instance } from "../AxiosApi";
import axios from "axios";
import * as Util from '../Util';
import spotlist from "../Setting/spot.json";
import './Direct.css';

function Direct() {
    if (window.location.search === "")
        window.location.href = '/notfound';

    const nav = useNavigate();
    const url = new URL(window.location.href);
    const planet = parseInt(url.searchParams.get("planet"));
    const cluster = parseInt(url.searchParams.get("cluster"));
    const floor = parseInt(url.searchParams.get("floor"));
    const spot = parseInt(url.searchParams.get("spot"));
    let spot_str;
    let locate;

    if (planet === 1) {
        locate = "개포 ";
        if (cluster === 0 && 1 <= floor && floor <= 6) {
            spot_str = spotlist[floor][spot];
            if (floor === 6 && (spot === 1 || spot === 2))
                locate += "옥상 " + spot_str;
            else if (floor === 6 && (spot === 0 || spot === 3))
                locate += "지하 " + spot_str;
            else if (spot_str !== undefined)
                locate += floor + "층 " + spot_str;
            else
                nav('/notfound');
        } else
            nav('/notfound');
    } else if (planet === 2) {
        locate = "서초 ";
        if (floor === 0 && 7 <= cluster && cluster <= 10) {
            spot_str = spotlist[cluster][spot];
            if (spot_str !== undefined) {
                locate += cluster + "클 " + spot_str;
            }
            else
                nav('/notfound');
        } else
            nav('/notfound');
    } else
        nav('/notfound');

    async function setLocate() {
        let serverurl = "";
        document.getElementById("confirm").className += " loading";
        document.getElementById("confirm").innerHTML = "<br/>";
        await instance.get('auth/login')
            .then((res) => {
                serverurl = res.data;
            });
        await axios.get('/v2/login')
            .then(() => {
                instance.get('member/setting/locate').then(() => {
                    instance.post('member/setting/locate', {
                        planet: planet,
                        cluster: cluster,
                        floor: floor,
                        spot: spot_str
                    }).then(() => {
                        Util.Alert("설정 완료!");
                        nav('/Main');
                    });
                }).catch((err) => {
                    if (err.response.status === 403) {
                        Util.Alert("클러스터 외부에 있으므로 수동 자리 정보를 등록할 수 없습니다.");
                    } else if (err.response.status === 409) {
                        Util.Alert("자동 자리 정보가 존재하여 수동 자리 정보를 등록할 수 없습니다.");
                    } else if (err.response.status === 503) {
                        Util.Alert("출퇴근 확인 중 일시적인 오류로 인하여 수동 자리 정보를 등록할 수 없습니다.");
                    }
                    document.getElementById("confirm").classList.remove('loading');
                    document.getElementById("confirm").innerText = "설정";
                });
            }).catch((err) => {
                localStorage.setItem('direct', url);
                const errData = err.response.data;
                if (errData.hasOwnProperty('data'))
                    nav('/Agree', {state : errData.data});
                else
                    window.location.href = serverurl;
            });
    }

    return (
        <div id="direct">
            <div id="wrapper">
                <div id="content">
                    <p>{locate}</p>
                    <p>으로 설정하시겠습니까?</p>
                </div>
                <img src="img/character.svg" alt="img"></img>
                <div id="buttons">
                    <button id="confirm" className="button" onClick={()=>{setLocate()}}>설정</button>
                    <button id="cancel" className="button" onClick={()=>{nav('/')}}>취소</button>
                </div>
            </div>
        </div>
    )
}

export default Direct;