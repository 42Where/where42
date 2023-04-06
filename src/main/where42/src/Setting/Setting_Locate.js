import spot from "./spot.json";
import {useLocation, useNavigate} from "react-router";
import {instance} from "../AxiosApi";
import * as Util from '../Util';

export function SettingFloor() {
    const loc = useLocation();
    const nav = useNavigate();
    const planet = loc.state?.planet;

    return (
        <div id="SettingFloor">
            <button id="Back" onClick={()=>{nav('/Setting')}}></button>
            <button id="Home" onClick={()=>{nav('/Main')}}></button>
            <div id="Comment">층 수 선택</div>
            <div id="BoxWrapper">
                <Box cap="1층" floor={1} planet={planet}/>
                <Box cap="2층" floor={2} planet={planet}/>
                <Box cap="3층" floor={3} planet={planet}/>
                <Box cap="4층" floor={4} planet={planet}/>
                <Box cap="5층" floor={5} planet={planet}/>
                <Box cap="B1/옥상" floor={6} planet={planet}/>
            </div>
        </div>
    )
}

export function SettingCluster() {
    const loc = useLocation();
    const nav = useNavigate();
    const planet = loc.state?.planet;

    return (
        <div id="SettingCluster">
            <button id="Back" onClick={()=>{nav('/Setting')}}></button>
            <button id="Home" onClick={()=>{nav('/Main')}}></button>
            <div id="Comment">클러스터 선택</div>
            <div id="BoxWrapper">
                <Box cap="7 클러스터" cluster={7} planet={planet}/>
                <Box cap="8 클러스터" cluster={8} planet={planet}/>
                <Box cap="9 클러스터" cluster={9} planet={planet}/>
                <Box cap="10 클러스터" cluster={10} planet={planet}/>
            </div>
        </div>
    )
}

export function SettingSpot() {
    const loc = useLocation();
    const nav = useNavigate();
    const spotNum = loc.state?.locate;

    return (
        <div id="SettingSpot">
            {
                spotNum < 7 ? (
                    <>
                        <button id="Back" onClick={()=>{nav('/Setting/SetFloor', {state: {planet:1}})}}></button>
                    </>
                ) : (
                    <>
                        <button id="Back" onClick={()=>{nav('/Setting/SetCluster', {state: {planet:2}})}}></button>
                    </>
                )
            }
            <button id="Home" onClick={()=>{nav('/Main')}}></button>
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

function Box(props) {
    const nav = useNavigate();

    if (props.floor) {
        return (
            <div className='Box' onClick={(e) => {
                localStorage.setItem('locate', JSON.stringify({
                    planet: props.planet, floor: props.floor, cluster: 0
                }));
                nav("/Setting/SetSpot", {state: {locate: props.floor}});
            }}>
                <div className='BoxCap'>{props.cap}</div>
            </div>
        )
    }
    else if (props.cluster) {
        return (
            <div className='Box' onClick={() => {
                localStorage.setItem('locate', JSON.stringify({
                    planet: props.planet, floor: 0, cluster: props.cluster
                }));
                nav("/Setting/SetSpot", {state: {locate: props.cluster}});
            }}>
                <div className='BoxCap'>{props.cap}</div>
            </div>
        )
    }
    else {
        return (
            <div className='Box' onClick={() => {
                let locate = JSON.parse(localStorage.getItem('locate'));
                console.log(locate.planet, locate.cluster, locate.floor, props.cap);
                instance.post('member/setting/locate', {
                    planet: locate.planet,
                    cluster: locate.cluster,
                    floor: locate.floor,
                    spot: props.cap
                }).then(() => {
                    Util.Alert("수정 완료!");
                    nav("/Setting");
                });
            }}>
                <div className='BoxCap'>{props.cap}</div>
            </div>
        )
    }
}