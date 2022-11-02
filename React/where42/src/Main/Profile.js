const Profile = (props) => {
    const info = props.info;
    const locate = CombineLocate(info.locate, info.inOutState);
    let meOrNot = null;

    if (props.me) {
        meOrNot = ( 
        <div id="SettingWrapper">
            <button id="Setting"></button>
        </div>
        )
    }
    
    return (
        <div className="Profile">
            <div className="Photo">
                {/* <img src={info.image_url} alt="user-face"></img> */}
                <img src="img/erase.png" alt="user-face"></img>
            </div>
            <div className="Info">
                <div className="Name">{info.name}</div>
                <div className="Locate">{locate}</div>
                <div className="Msg">{info.msg}</div>
            </div>
            {meOrNot}
        </div>
    );
};

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