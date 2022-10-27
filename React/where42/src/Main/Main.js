import React from 'react';
import { useMediaQuery } from 'react-responsive';
import './Main.css';
import './Main_Desktop.css';
import './Main_Mobile.css';
import Profile from './profile';
import sample from './sample.json';

function Main() {
    const isMobile = useMediaQuery({ query: '(max-width: 930px'});
    const isDesktop = useMediaQuery({ query: '(min-width: 931px'});

    function Common() {
        return (
            <div id="Wrapper">
                <button id="Burger"></button>
                <div id="Logo">
                    {/* 로고 선택시 메인페이지 이동하도록 할건지? */}
                    <img src="img/logo_simple.svg" alt="logo"></img>
                    {isMobile && <p>42서울 자리 찾기 서비스</p>}
                </div>
                <div id="my-Profile">
                    <button id="Setting"></button>
                    <Profile key={sample.memeberInfo.id} Info={sample.memeberInfo}/>
                </div>
                <Groups Info={sample.groupInfo}/>
            </div>
        )
    }

    return (
        <div id="Main">
            {isMobile && <div id="Mobile"><Common/></div>}
            {isDesktop && <div id="Desktop"><Common/></div>}
        </div>
    )
}

function Groups({Info}){
    // const Info = props.Info;
    return (
        <>
        {Info.map(group=>(
            <div key={group.groupName}>
                <div id="group-Name">{group.groupName} ({group.count})</div>
                <GroupProfile Info={group}/>
            </div>
        ))}
        </>
    )
}

function GetFrinedInfo (data, value){
    // 대체로 json 파일에서 type : 성공 실패 여부인데 넣을건지?
    // if (data.TYPE=="S"){
    return data.filter(function(Friend){
        return Friend.name === value;
    });
    // }
}

function GroupProfile(props) {
    const Info = props.Info;
    // // 여기에서 한개의 groupfriends 마다 group friend를 검색해야함 완료!
    const FriendList = Info.groupFriends.map(Friend => {
        const FriendInfo = GetFrinedInfo(sample.groupFriendInfo, Friend);
        return <Profile key={Info.groupName+Friend} Info={FriendInfo[0]}/>
    });

    return (
        <div id="profile-Wrapper">
            {FriendList}
        </div>
    )
}

export default Main;

// 형광펜이 모바일 사이즈에서 위로 올라가있음 ㅜㅜ
