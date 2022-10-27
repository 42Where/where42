import React from "react";

const Profile = (props) => {
    const Info = props.Info;
    // locate 분기나눠서 하나의 string으로 만들기 완료!
    const locate = CombineLocate(Info.locate);
    // circle 이미지를 출근/퇴근에 따라서 바꿔주기 완료!
    const background = GetBackgroundImage(Info.inOutState, Info.locate.spot);
    
    return (
        <div id="Profile">
            <div id="Photo">
                {/* url : https://cdn.intra.42.fr/users/{Info.name}.jpg */}
                <img src="img/erase.png" alt="user-face"></img>
            </div>
            <div id="Info">
                <div id="name-Circle">
                    <div id="Name">{Info.name}</div>
                    <div id="Circle" style={background}></div>
                </div>
                <div id="Locate">{locate}</div>
                <div id="Msg">{Info.msg}</div>
            </div>
        </div>
    );
};

function GetBackgroundImage(inOutState, spot)
{
    //inoutstate가 비회원일 경우엔 어떻게 오지..?
    let background = {backgroundImage:"url('img/circle_grey.svg')"};
    if (inOutState == 0)
        background = {backgroundImage:"url('img/circle_green.svg')"};
    return background;
}

function CombineLocate(locate){
    let position = "";
    if (locate.planet == 0)
        return position;
    if (locate.planet == 1)
        position += '개포 ';
    else if (locate.planet == 2)
        position += '서초 ';
    if (locate.floor > 0)
        position += locate.floor.toString() + '층 ';
    if (locate.floor == -1)
        position += '지하 1층 ';
    if (locate.cluster != 0)
        position += locate.cluster.toString() + '클러스터 '; // '클러스터' 라고 쓸건지?
    if (locate.spot != null)
        position += locate.spot.toString();
    return position;
}

export default Profile;