import sample from './sample.json';
import Profile from './Profile';

function Groups({Info}){
    // const Info = props.Info;
    return (
        <>
        {Info.map(group=>(
            <div key={group.groupName}>
                <MakeGroupName Name={group.groupName} Count={group.count}/>
                <GroupProfile Info={group}/>
            </div>
        ))}
        </>
    )
}

function MakeGroupName(props)
{
    let groupName;

    if (props.Name === "기본")
        groupName = "친구 목록";
    else
        groupName = props.Name;

    return (
        <div id="group-Name">{groupName} ({props.Count})</div>
    )
}

function GetFrinedInfo(data, value){
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
        return <Profile key={Info.groupName+Friend} Info={FriendInfo[0]} Me={0}/>
    });

    return (
        <div id="profile-Wrapper">
            {FriendList}
        </div>
    )
}

export default Groups;