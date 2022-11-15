import React from 'react';
import { useState } from 'react';
import { useMediaQuery } from 'react-responsive';
import select from './select.json';

const Profile = (props) => {
    const info = props.info;
    const [detail, setDetail] = useState(false);
    const isMobile = useMediaQuery({ query: '(max-width: 930px'});
    const isDesktop = useMediaQuery({ query: '(min-width: 931px'});

    function FriendClick(e){
        //api 요청 post (memberId, friendName)
        if (isDesktop)
        {
            e.target.innerText = '친구 추가 완료';
        }
        else if (isMobile)
        {
            e.target.style = "background-image: url('img/friend_check.svg')";
        }
        e.target.className = "AddDone";
    }

    let friendOrNot;
    if (isDesktop)
        friendOrNot = (<button className={info.friend? "AddDone" : "AddFriend"} onClick={FriendClick} >{info.friend? '친구 추가 완료' : '친구 추가'}</button>)
    else if (isMobile)
        friendOrNot = (<button className={info.friend? "AddDone" : "AddFriend"} onClick={FriendClick}></button>)

    const DetailClick = (e) => {
        //api 요청 get info 정보 그대로 넘기기
        if (isMobile)
        {
            if (detail === true)
                e.target.style = "background-image: url('img/detail_off.svg')";
            else
                e.target.style = "background-image: url('img/detail_on.svg')";
        }
        setDetail(!detail);
    }

    let detailCheck;

    if (isDesktop && detail === false)
        detailCheck = (<button className="CheckSpot" onClick={DetailClick}>정보 확인</button>);
    else if (isDesktop && detail === true)
        detailCheck = null;
    else if (isMobile)
        detailCheck = (<button className="CheckSpot" onClick={DetailClick}></button>);

    return (
        <div className="Profile">
            <div className="Photo">
                <img src={info.image_url} alt="user-face"></img>
            </div>
            <div className="Info">
                <div className="Name">{info.login}</div>
                {detail === true? <Detail info={info}/> : null}
            </div>
            <div className="ButtonWrapper">
                {friendOrNot}
                {detailCheck}
            </div>
        </div>
    );
};

const Detail = (props) => {
    // const info = props.info;
    const info = select;
    const locate = CombineLocate(info.locate, info.inOutState);

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
        if (locate.planet === 0)
            position = "클러스터 내";
        if (locate.planet === 1)
            position += '개포 ';
        else if (locate.planet === 2)
            position += '서초 ';
        if (locate.floor > 0)
            position += locate.floor.toString() + '층 ';
        if (locate.floor === -1)
            position += '지하 1층 ';
        if (locate.cluster !== 0)
            position += locate.cluster.toString() + '클 ';
        if (locate.spot !== null)
            position += locate.spot.toString();
    }
    return position;
}

export default Profile;