import {useEffect, useState} from "react";
import {useLocation, useNavigate} from "react-router";
import {Link} from "react-router-dom";
import {instance} from "../AxiosApi";
import * as Util from '../Util';

export function SettingGnF() {
    const nav = useNavigate();
    return (
        <div id="SettingGnF">
            <button id="Back" onClick={()=>{nav('/Setting')}}></button>
            <button id="Home" onClick={()=>{nav('/Main')}}></button>
            <div id="Comment">그룹/친구 관리</div>
            <div id="BoxWrapper">
                <Link to="/Setting/SetGroup">
                    <div className='Box'>
                        <div className='BoxCap'>그룹 관리</div>
                    </div>
                </Link>
                <Link to="/Setting/SetFriend">
                    <div className='Box'>
                        <div className='BoxCap'>친구 삭제</div>
                    </div>
                </Link>
            </div>
        </div>
    )
}

export function SettingGroup() {
    const nav = useNavigate();
    const [arr, setArr] = useState(null);
    useEffect(() => {
        instance.get('group')
            .then((res) => {
                setArr(res.data);
            });
    }, []);
    const [name, setName] = useState("");
    const handleChange = ({target : {value}}) => setName(value);
    const handleSubmit = async (e) => {
        e.preventDefault();
        if (name === "즐겨찾기" || name === "기본" || name === "친구 목록" || name === "" ||
            name.split(' ').length - 1 === name.length) {
            Util.Alert("사용할 수 없는 그룹명입니다.");
        } else {
            try {
                await instance.post('group', null, {params: {groupName: name}});
                Util.AlertReload("그룹을 생성하였습니다.");
            } catch (err) {
                if (err.response.status === 409) {
                    Util.Alert("이미 존재하는 그룹명입니다.");
                }
            }
        }
    }

    return (
        <div id="SettingGroup">
            <button id="Back" onClick={()=>{nav('/Setting/SetGnF')}}></button>
            <button id="Home" onClick={()=>{nav('/Main')}}></button>
            <div id="Comment">그룹 관리</div>
            <form onSubmit={handleSubmit}>
                <input type="text" maxLength="10" placeholder="그룹명은 10자까지 입력 가능합니다." spellcheck="false" value={name} onChange={handleChange}/>
                <button type="submit">추가</button>
            </form>
            <div id="GroupList">
                {
                    arr && arr.map((group, index) => {
                        return <GroupList name={group.groupName} id={group.groupId} key={index}/>
                    })
                }
            </div>
        </div>
    )
}

export function SettingFriend(props) {
    const nav = useNavigate();
    const loc = useLocation();
    const groupInfo = loc.state;

    let apiUrl;
    let doneUrl;
    let comment;
    if (props.type === "add") {
        apiUrl = 'groupFriend/notIncludes/group/' + groupInfo.id;
        comment = "추가";
        doneUrl = "/Setting/SetGroup";
    } else if (props.type === "del") {
        apiUrl = 'groupFriend/includes/group/' + groupInfo.id;
        comment = "삭제";
        doneUrl = "/Setting/SetGroup";
    } else if (props.type === "fDel") {
        apiUrl = 'groupFriend/friendList';
        comment = "삭제";
        doneUrl = "/Setting";
    }

    const [arr, setArr] = useState(null);
    useEffect(() => {
        instance.get(apiUrl)
            .then((res) => {
                setArr(res.data);
            }).catch((err)=>{
                if (err?.response?.status === 400)
                    Util.Alert("잘못된 접근입니다. 잠시 후 다시 시도해주세요.").then((res)=>{
                        nav("/Setting/SetGroup");
                    });
        });
    }, []);

    const [list, setList] = useState(new Set());
    const addList = (user, checked) => {
        if (checked) {
            list.add(user);
            setList(list);
        }
        else if (!checked && list.has(user)) {
            list.delete(user);
            setList(list);
        }
    }

    const handleSubmit = (event) => {
        event.preventDefault();
        if (list.size > 0)
        {
            Util.Confirm(comment + ' 하시겠습니까?', comment).then((res) => {
                if (res && (res.isConfirmed !== false))
                {
                    instance.post(apiUrl, Array.from(list))
                        .then(() => {
                            Util.Alert(comment + " 완료!");
                            nav(doneUrl);
                        }).catch((err)=>{
                            if (err?.response?.status === 400)
                                Util.Alert("잘못된 접근입니다. 잠시후 다시 시도해주세요");
                    });
                }
            });
        }
    }

    return (
        <div id="SettingFriend">
            {
                props.type === "fDel" ? (
                    <>
                        <button id="Back" onClick={()=>{nav('/Setting/SetGnF')}}></button>
                    </>
                ) : (
                    <>
                        <button id="Back" onClick={()=>{nav('/Setting/SetGroup')}}></button>
                    </>
                )
            }
            <button id="Home" onClick={()=>{nav('/Main')}}></button>
            {
                props.type === "fDel" ? (
                    <>
                        <div id="Comment">현재 친구 목록</div>
                        <div id="Comment2">삭제할 친구를 선택한 후 '삭제' 버튼을 눌러주세요.</div>
                    </>
                ) : (
                    <>
                        <div id="Comment">{groupInfo.groupName}</div>
                        <div id="Comment2">{comment}할 친구를 선택해 주세요.</div>
                    </>
                )
            }
            <form onSubmit={handleSubmit}>
                <div id="MemberWrapper">
                    {
                        (arr && arr.length !== 0) ? arr.map((value, index) => (
                            <MemberList user={value} addList={addList} key={index}/>
                        )) : <div id="NoFriend">{comment} 가능한 친구가 없습니다.</div>
                    }
                </div>
                <button type="submit">{comment}</button>
            </form>
        </div>
    )
}

function GroupList(props) {
    const delGroup = () => {
        Util.Confirm('정말 삭제하시겠습니까?', '삭제').then((res) => {
            if (res && (res.isConfirmed !== false))
            {
                instance.delete('group/' + props.id)
                    .then(() => {
                        Util.AlertReload("'" + props.name + "' 그룹을 삭제하였습니다.");
                    }).catch((err)=>{
                        if (err?.response?.status === 400)
                            Util.AlertReload("잘못된 접근입니다. 잠식후 다시 시도해주세요.");
                });
            }
        });
    }
    const modGroup = () => {
        Util.AlertInput("그룹명을 입력해 주세요.", props.name).then((res) => {
            let gName = res.value;
            if (gName === "즐겨찾기" || gName === "기본" || gName === "친구 목록" || gName === "" ||
            gName.split(' ').length - 1 === gName.length) {
                Util.Alert("사용할 수 없는 그룹명입니다.").then(() => {
                    setTimeout(modGroup, 100);
                });
            } else if (gName.length > 10) {
                Util.Alert("그룹명은 10자까지 입력 가능합니다.").then(() => {
                    setTimeout(modGroup, 100);
                });
            } else {
                instance.post('group/' + props.id, null, {params: {changeName: gName}})
                    .then(() => {
                        Util.AlertReload("그룹명이 변경되었습니다.");
                    }).catch((err) => {
                        if (err?.response?.status === 400)
                            Util.AlertReload("잘못된 접근입니다. 잠시 후 다시 시도해주세요.");
                        else
                            Util.AlertReload("중복된 이름의 그룹이 존재합니다.");
                    });
            }
        })
        
    }
    
    return (
        <div className='Group'>
            <input type="text" value={props.name} disabled/>
            <div className='GroupButtons'>
                <button onClick={modGroup}></button>
                <button onClick={delGroup}></button>
            </div>
            <div className='FriendButtons'>
                <Link to="/Setting/SetGroupAdd" state={{id: props.id, groupName: props.name}}>
                    <button></button>
                </Link>
                <Link to="/Setting/SetGroupDel" state={{id: props.id, groupName: props.name}}>
                    <button></button>
                </Link>
            </div>
        </div>
    )
}

function MemberList(props) {
    const [checked, setChecked] = useState(false);
    useEffect(() => {props.addList(props.user, checked)}, [props, checked]);

    return (
        <div className='User'>
            <label><input type="checkbox" onChange={()=>{setChecked(!checked)}}/>{props.user}</label>
        </div>
    )
}