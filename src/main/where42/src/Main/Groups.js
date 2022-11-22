import Profile from './Profile';

function Groups(props){
    const groupInfo = props.groupInfo;
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

function GetFriendInfo(friendInfo, name){
    return friendInfo.filter((person)=>{
        return person.name === name;
    });
}

function GroupProfile(props) {
    const groupInfo = props.groupInfo;
    const friendList = groupInfo.groupFriends.map(friend => {
        const oneFriendInfo = GetFriendInfo(props.friendInfo, friend);
        return <Profile key={groupInfo.groupName + friend} info={oneFriendInfo[0]} me={0}/>
    });

    return (
        <div className="ProfileWrapper">
            {friendList}
        </div>
    )
}

export default Groups;