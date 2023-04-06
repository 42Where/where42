import React, {useState} from 'react';
import { useMediaQuery } from 'react-responsive';
import {instance} from "../AxiosApi";
import {CombineLocate} from "../Main/MainProfile";
import * as Util from '../Util';

const SearchProfile = (props) => {
    const isMobile = useMediaQuery({ query: '(max-width: 930px)'});
    const isDesktop = useMediaQuery({ query: '(min-width: 931px)'});
    
    const info = props.info;
    if (info.img === "null" || info.img === null)
        info.img = 'img/character.svg';
    const memberId = localStorage.getItem('name');
    const [detail, setDetail] = useState(null);
   
    function FriendClick(e){
        if (info.friend === true)
            return ;
        if (info.name === memberId)
        {
            Util.Alert("나는 우주를 여행하는 당신의 영원한 친구입니다.");
            return ;
        }
        Util.Confirm("친구로 등록하시겠습니까?", "등록").then((res)=>{
            if (res && (res.isConfirmed !== false))
            {
                instance.post('groupFriend', null, {params: {friendName : info.name, img: info.img}});
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
        })
    }

    let friendOrNot;

    if (isDesktop)
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
        if (info.name === "WHERE42")
        {
            setDetail(info);
            return ;
        }
        if (info.location === "parsed")
        {
            setDetail(info);
            return ;
        }
        const body = info;
        instance.post('search/select', body)
            .then((response)=>{
                setDetail(response.data);
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
                <img src={info.img} alt="user-face"></img>
            </div>
            <div className="Info">
                <div className="Name">{info.name}</div>
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
    const locate = CombineLocate(info.locate, info.inOrOut, info.eval);

    return (
        <>
            <div className="Locate">{locate}</div>
            <div className="Msg">{info.msg}</div>
        </>
    )
}

export default SearchProfile;