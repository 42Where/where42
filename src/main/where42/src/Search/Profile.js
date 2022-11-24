import React from 'react';
import { useState } from 'react';
import { useMediaQuery } from 'react-responsive';
import axios from 'axios';
import {useNavigate} from "react-router";

const Profile = (props) => {
    const nav = useNavigate();
    const isMobile = useMediaQuery({ query: '(max-width: 930px'});
    const isDesktop = useMediaQuery({ query: '(min-width: 931px'});
    
    const info = props.info;
    const memberId = props.memberId;
    const [detail, setDetail] = useState(null);
   
    function FriendClick(e){
        if (info.friend === true)
            return ;
        // axios.post('v1/groupFriend',null,{params: {friendName : info.login, image : info.image}
        axios.post('v1/groupFriend',null,{params: {friendName : info.login}
        }).then((response)=>{
            if (response.status === 201) //친구추가 성공
                console.log(response.data)
        }).catch((Error)=>{
            if (Error.response.status === 401)
                //세션 없음 401에러 -> 로그인으로 보내서 재 로그인하게하기
                nav('/Login');
            console.log(Error);
        })
        if (isDesktop)
        {
            e.target.innerText = '친구 추가 완료';
        }
        else if (isMobile)
        {
            e.target.style = "background-image: url('img/friend_check.svg')";
        }
        e.target.className = "AddDone";
        info.friend = true;
    }

    let friendOrNot;

    console.log(info.login);
    console.log(memberId);
    if (info.login === memberId)
        friendOrNot = <></>
    else if (isDesktop)
        friendOrNot = (<button className={info.friend? "AddDone" : "AddFriend"} onClick={FriendClick}>{info.friend? '친구 추가 완료' : '친구 추가'}</button>)
    else if (isMobile)
        friendOrNot = (<button className={info.friend? "AddDone" : "AddFriend"} onClick={FriendClick}></button>)

    const DetailClick = (e) => {
        if (isMobile)
        {
            if (detail != null) {
                e.target.style = "background-image: url('img/detail_off.svg')";
                setDetail(null);
                return ;
            }
            else
                e.target.style = "background-image: url('img/detail_on.svg')";
        }
        const body = {login : info.login , image : info.image, msg : info.msg, inOrOut : info.inOrOut, location : info.location, friend : info.friend};
        axios.post('v1/search/select', body)
            .then((response)=>{
                setDetail(response.data);
            }).catch((Error)=>{
                // console.error(Error)
        })
    }

    let detailCheck;

    if (isDesktop && detail === null)
        detailCheck = (<button className="CheckSpot" onClick={DetailClick}>정보 확인</button>);
    else if (isDesktop && detail != null)
        detailCheck = null;
    else if (isMobile)
        detailCheck = (<button className="CheckSpot" onClick={DetailClick}></button>);

    return (
        <div className="Profile">
            <div className="Photo">
                <img src={info.image.link} alt="user-face"></img>
            </div>
            <div className="Info">
                <div className="Name">{info.login}</div>
                {detail != null? <Detail info={detail}/> : null}
            </div>
            <div className="ButtonWrapper">
                {friendOrNot}
                {detailCheck}
            </div>
        </div>
    );
};

const Detail = (props) => {
    const info = props.info;
    const locate = CombineLocate(info.locate, info.inOrOut);
    return (
        <>
            <div className="Locate">{locate}</div>
            <div className="Msg">{info.msg}</div>
        </>
    )
}

function CombineLocate(locate, inOutState){
    let position = "";
    if (inOutState === 2)
        position = "자리 정보 없음";
    else if (inOutState === 0)
        position = "퇴근";
    else
    {
        //inoutstate가 1이고 planet이 0인 경우 없음? : 확인 필요
        if (locate.planet === 1)
            position = '개포 ';
        else if (locate.planet === 2)
            position = '서초 ';
        if (locate.floor === 0)
            position += "클러스터 내";
        else if (locate.floor > 0)
            position += locate.floor.toString() + '층 ';
        else if (locate.floor === -1)
            position += '지하 1층 ';
        if (locate.cluster !== 0)
            position += locate.cluster.toString() + '클 ';
        if (locate.spot !== null)
            position += locate.spot.toString();
    }
    return position;
}

export default Profile;