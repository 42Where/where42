import React from 'react';
import { useState } from 'react';
import { useMediaQuery } from 'react-responsive';
import select from './select.json';

const Profile = (props) => {
    const isMobile = useMediaQuery({ query: '(max-width: 930px'});
    const isDesktop = useMediaQuery({ query: '(min-width: 931px'});
    
    const info = props.info;
    const [detail, setDetail] = useState(0);
    let friendOrNot;

    if (info.friend)
        friendOrNot = (<button className="AddDone">친구 추가 완료</button>);
    else
        friendOrNot = (<button className="AddFriend">친구 추가</button>);

    function DetailClick()
    {
        const button = document.getElementsByClassName('CheckSpot');
        if (isMobile)
        {
            if (detail === 0)
            {
                button[0].style.backgroundImage = "url('img/detail_on.svg')";
                setDetail(1);
            }
            else
            {
                button[0].style.backgroundImage = "url('img/detail_off.svg')";
                setDetail(0);
            }
        }
        else if (isDesktop)
        {
            if (detail === 0)
                setDetail(1);
        }
    }
    return (
        <div className="Profile">
            <div className="Photo">
                <img src={info.image_url} alt="user-face"></img>
            </div>
            <div className="Info">
                <div className="Name">{info.login}</div>
                {detail === 1 ? <Detail info={info}/> : null}
            </div>
            <div className="ButtonWrapper">
                {friendOrNot}
                {detail === 0 || isMobile? <button className="CheckSpot" onClick={DetailClick}>자리 확인</button> : null}
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