import {useEffect, useRef, useState} from "react";
import {useLocation, useNavigate} from "react-router";
import {Link} from "react-router-dom";
import axios from "axios";

export function SettingGnF() {
    return (
        <div id="SettingGnF">
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
        axios.get('/v1/group')
            .then((res) => {
                setArr(res.data);
            }).catch(() => {
                nav("/Login");
        });
    }, []);
    const [name, setName] = useState("");
    const handleChange = ({target : {value}}) => setName(value);
    const handleSubmit = async (e) => {
        e.preventDefault();
        if (name === "즐겨찾기" || name === "기본" || name === "친구 목록") {
            alert("사용할 수 없는 그룹명입니다.");
        } else {
            try {
                await axios.post('/v1/group', null, {params: {groupName: name}});
                alert("그룹을 생성하였습니다.");
                window.location.reload();
            } catch (err) {
                if (err.response.status === 401) {
                    nav('/Login');
                } else if (err.response.status === 409) {
                    alert("이미 존재하는 그룹명입니다.");
                }
            }
        }
    }

    return (
        <div id="SettingGroup">
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
        apiUrl = '/v1/groupFriend/notIncludes/group/' + groupInfo.id;
        comment = "추가";
        doneUrl = "/Setting/SetGroup";
    } else if (props.type === "del") {
        apiUrl = '/v1/groupFriend/includes/group/' + groupInfo.id;
        comment = "삭제";
        doneUrl = "/Setting/SetGroup";
    } else if (props.type === "fDel") {
        apiUrl = '/v1/groupFriend/friendList';
        comment = "삭제";
        doneUrl = "/Setting";
    }

    const [arr, setArr] = useState(null);
    useEffect(() => {
        axios.get(apiUrl)
            .then((res) => {
                setArr(res.data);
            }).catch(() => {
                nav("/Login");
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
        if (list.size > 0 && window.confirm(comment + " 하시겠습니까?")) {
            axios.post(apiUrl, Array.from(list))
                .then(() => {
                    alert(comment + " 완료!");
                    nav(doneUrl);
                }).catch(() => {
                nav("/Login");
            });
        }
    }

    return (
        <div id="SettingFriend">
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
                        )) : <div id="NoFriend">{comment}할 친구가 없습니다.</div>
                    }
                </div>
                <button type="submit">{comment}</button>
            </form>
        </div>
    )
}

function GroupList(props) {
    const nav = useNavigate();
    const inputRef = useRef(null);
    const [name, setName] = useState(props.name);
    const delGroup = () => {
        if (window.confirm("정말 삭제하시겠습니까?")) {
            axios.delete('/v1/group/' + props.id)
                .then(() => {
                    alert("'" + props.name + "' 그룹을 삭제하였습니다.");
                    window.location.reload();
                });
        }
    }
    const modGroup = () => {
        if (inputRef.current.disabled === true) {
            inputRef.current.disabled = false;
            inputRef.current.focus();
        }
        else {
            inputRef.current.disabled = true;
            if (name === "즐겨찾기" || name === "기본" || name === "친구 목록") {
                alert("사용할 수 없는 그룹명입니다.");
                window.location.reload();
            } else {
                axios.post('/v1/group/' + props.id, null, {params: {changeName: name}})
                    .then(() => {
                        nav('/Setting/SetGroup');
                    }).catch(() => {
                    alert("중복된 이름의 그룹이 존재합니다.");
                    window.location.reload();
                });
            }
        }
    }
    const handleChange = ({target : {value}}) => setName(value);

    return (
        <div className='Group'>
            <input type="text" maxLength="10" value={name} spellcheck="false" onChange={handleChange} ref={inputRef} disabled/>
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