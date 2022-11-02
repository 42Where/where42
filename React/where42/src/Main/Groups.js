import sample from './sample.json';
import Profile from './Profile';

function Groups(props){
    const info = props.info;
    return (
        <>
        {info.map(group=>(
            <div key={group.groupName}>
                <MakeGroupName name={group.groupName} count={group.count}/>
                <GroupProfile info={group}/>
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
        <div id="GroupName">{groupName} ({props.count})</div>
    )
}

function GetFrinedInfo(data, value){
    return data.filter(function(friend){
        return friend.name === value;
    });
}

function GroupProfile(props) {
    const info = props.info;
    const friendList = info.groupFriends.map(friend => {
        const friendInfo = GetFrinedInfo(sample.groupFriendInfo, friend);
        return <Profile key={info.groupName + friend} info={friendInfo[0]} me={0}/>
    });

    return (
        <div id="ProfileWrapper">
            {friendList}
        </div>
    )
}

export default Groups;