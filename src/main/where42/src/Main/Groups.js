import MainProfile from './MainProfile';
import {useState} from "react";

function Groups(props){
    const groupInfo = props.groupInfo;
    const toggle = props.toggle;

    return (
        <>
        {groupInfo.map(group=>(
            <div key={group.groupName}>
                <OneGroup group={group} friendInfo={props.friendInfo} toggle={toggle}/>
            </div>
        ))}
        </>
    )
}

function OneGroup(props) {
    const group = props.group;
    const [show, setShow] = useState(true);
    const [commuteCount, setCommuteCount] = useState(0);

    let groupName;
    if (group.groupName === "기본")
        groupName = "친구 목록";
    else
        groupName = group.groupName;

    return (
        <>
            <div className="GroupName">{groupName} ( {commuteCount} / {group.count} )&nbsp;
                <img src="img/toggle_down.svg" alt="groupToggle" className={show? "GroupToggle" : "GroupToggle rotate90" }
                     onClick={()=>setShow(!show)} ></img>
            </div>
            {show && <GroupProfile groupInfo={group} friendInfo={props.friendInfo} toggle={props.toggle} setCountFunction={setCommuteCount}/>}
        </>
    )
}

function GetFriendInfo(friendInfo, name){
    return friendInfo.filter((person)=>{
        return person.name === name;
    });
}

function GroupProfile(props) {
    const groupInfo = props.groupInfo;
    const toggle = props.toggle;
    let count = 0;

    const friendList = groupInfo.groupFriends.map(friend => {
        const oneFriendInfo = GetFriendInfo(props.friendInfo, friend);
        if (oneFriendInfo[0].inOrOut === 1)
            count += 1;
        if (toggle && oneFriendInfo[0].inOrOut !== 1)
            return (null);
        else
            return <MainProfile key={groupInfo.groupName + friend} info={oneFriendInfo[0]} me={0}/>
    });
    props.setCountFunction(count);

    return (
        <div className="ProfileWrapper">
            {friendList}
        </div>
    )
}

export default Groups;