import Profile from './Profile';

function Groups(props){
    const groupInfo = props.groupInfo;
    // console.log(groupInfo);
    //그룹 카운트가 안넘어옴 ㅜ 안보내주는건가 아니면 가먹은건가
    return (
        <>
        {groupInfo.map(group=>(
            <div key={group.groupName}>
                <MakeGroupName name={group.groupName} count={group.count}/>
                <GroupProfile groupInfo={group} friendInfo={props.friendInfo}/>
            </div>
        ))}
        </>
    )
}

function MakeGroupName(props)
{
    let groupName;

    if (props.name === "기본")
        groupName = "친구 목록";
    else
        groupName = props.name;
    return (
        <div className="GroupName">{groupName} ({props.count})</div>
    )
}

function GetFrinedInfo(data, value){
    return data.filter(function(friend){
        return friend.name === value;
    });
}

function GroupProfile(props) {
    const groupInfo = props.groupInfo;
    const friendList = groupInfo.groupFriends.map(friend => {
        const oneFriendInfo = GetFrinedInfo(props.friendInfo, friend);
        return <Profile key={groupInfo.groupName + friend} info={oneFriendInfo[0]} me={0}/>
    });

    return (
        <div className="ProfileWrapper">
            {friendList}
        </div>
    )
}

export default Groups;