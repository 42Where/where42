import axios from "axios";
import {useState} from "react";
import './Admin.css';

function Admin(){
    const [sign, setSign] = useState(false);
    function AdminSign(){
        const [id, setId] = useState("");
        const [password, setPassword] = useState("");
        function SignClick(){
            axios.post('v1/admin/login', {name : id, password : password})
                .then((res)=>{
                    alert("로그인에 성공했습니다");
                    setSign(true);
            }).catch((Error)=>{
                if (Error.response.status === 401)
                    alert("로그인 실패. 아이디와 비밀번호를 확인해주세요");
                else
                    console.log(Error);
            });
        }
        const IdChange=(e)=>{
            setId(e.target.value);
        }
        const PasswordChange=(e)=>{
            setPassword(e.target.value);
        }
        return (
            <div id="AdminSign">
                <h1>Admin Log In</h1>
                <div className="id">
                    <input placeholder={"id"} value={id} onChange={IdChange} type={"text"}/><br/>
                </div>
                <div className="password">
                    <input type={"password"} id="password" placeholder="Password" value={password} onChange={PasswordChange}/><br/>
                </div>
                <div className="div_button">
                    <button className="button" id="login" type="button" onClick={SignClick}>Log In</button>
                </div>
            </div>
        )
    }

    function AdminShow(){
        const [memberName, setMemberName] = useState("");
        const NameChange = (e) => {
            setMemberName(e.target.value);
        }
        function MemberClick(){
            axios.delete('v1/memeber', {params: {name: memberName}})
                .then((res)=>{
                    alert(memberName + "member delete ok");
                })
        }
        function AdminTokenClick(){
            axios.get(  'v1/admin')
                .then((res) => {
                    alert("admin token ok");
                });
        }
        function InClusterClick(){
            axios.get('v1/incluster').then((res)=>{
                alert("incluster ok");
            })
        }
        function HaneClick(){
            axios.get('v1/hane').then((res)=>{
                alert("24Hane Token ok");
            })
        }
        function ImageClick(){
            axios.get('v1/image').then((res)=>{
                alert("Image DB ok");
            })
        }
        function FlashClick(){
            axios.delete('v1/flash').then((res)=>{
                alert("flash DB deleted");
            })
        }
        function LogoutClick() {
            axios.get('v1/logout').then((res) => {
                alert("Logout ok");
                setSign(false);
            })
        }

        return (
            <div id="AdminShow">
                <div className="MemberDelete">
                    <input id={"MemberInput"} value={memberName} onChange={NameChange}></input>
                    <button id={"MemberButton"} onClick={MemberClick}>Delete Member Input</button>
                </div>
                <div className="AdminToken">
                    <button id={"AdminToken"} onClick={AdminTokenClick}>admin token</button>
                </div>
                <div className="HaneToken">
                    <button id={"HaneToken"} onClick={HaneClick}>24hane token</button>
                </div>
                <div className="InCluster">
                    <button id={"InCluster"} onClick={InClusterClick}>incluster</button>
                </div>
                <div className="ImageDB">
                    <button id={"ImageDb"} onClick={ImageClick}>Image DB</button>
                </div>
                <div className="FlashDB">
                    <button id={"FlashDb"} onClick={FlashClick}>Flash DB</button>
                </div>
                <div className="Logout">
                    <button id={"Logout"} onClick={LogoutClick}>logout</button>
                </div>
            </div>
            )
    }
    return (
        <div id="Admin">
            {sign? <AdminShow/> : <AdminSign/>}
        </div>)
}

export default Admin;