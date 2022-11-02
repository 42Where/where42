const Profile = (props) => {
    const Info = props.Info;
    // locate 분기나눠서 하나의 string으로 만들기 완료!
    const locate = CombineLocate(Info.locate, Info.inOutState);
    // circle 이미지를 출근/퇴근에 따라서 바꿔주기 완료!
    // const background = GetBackgroundImage(Info.inOutState, Info.locate.spot);
    let meOrNot = null;
    if (props.Me) {
        meOrNot = ( 
        <div id="setting-Wrapper">
            <button id="Setting"></button>
        </div> 
        )
    }
    
    return (
        <div id="Profile">
            <div id="Photo">
                {/* url : https://cdn.intra.42.fr/users/{Info.name}.jpg */}
                <img src="img/erase.png" alt="user-face"></img>
            </div>
            <div id="Info">
                <div id="name-Circle">
                    <div id="Name">{Info.name}</div>
                    {/* <div id="Circle" style={background}></div> */}
                </div>
                <div id="Locate">{locate}</div>
                <div id="Msg">{Info.msg}</div>
            </div>
            {meOrNot}
        </div>
    );
};

function GetBackgroundImage(inOutState, spot)
{
    //inoutstate : 
    // 0 퇴근
    // 1 출근
    // 2 비회원
    let background = {backgroundImage:"url('img/circle_grey.svg')"};
    if (inOutState === "1")
        background = {backgroundImage:"url('img/circle_green.svg')"};
    return background;
}

function CombineLocate(locate, inOutState){
    let position = "";
    // if (inOutState === "2")
    // {
    //     position += "자리정보없음";
    //     return position;
    // }
    // else if (inOutState === "1")
    // {
    //     position += 
    // }
    if (locate.planet === "0")
        return position;
    if (locate.planet === "1")
        position += '개포 ';
    else if (locate.planet === "2")
        position += '서초 ';
    if (locate.floor > "0")
        position += locate.floor.toString() + '층 ';
    if (locate.floor === "-1")
        position += '지하 1층 ';
    if (locate.cluster !== "0")
        position += locate.cluster.toString() + '클러스터 '; // '클러스터' 라고 쓸건지?
    if (locate.spot !== null)
        position += locate.spot.toString();
    return position;
}

export default Profile;