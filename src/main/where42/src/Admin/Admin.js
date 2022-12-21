import axios from "axios";
import {useState} from "react";
import './Admin.css';
import Swal from 'sweetalert2'

function Admin(){
    const [sign, setSign] = useState(false);
    function AdminSign(){
        const [id, setId] = useState("");
        const [password, setPassword] = useState("");
        function SignClick(){
            axios.post('v1/admin/login', {name : id, passwd : password})
                .then((res)=>{
                    setSign(true);
            }).catch((Error)=>{
                if (Error.response.status === 401)
                    Swal.fire("로그인 실패.\n 아이디와 비밀번호를 확인해주세요");
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
        const PasswordKeyDown=(event)=>{
            let charCode = event.keyCode;
            if (charCode === 13)
                SignClick();
        }
        return (
            <div id="AdminSign">
                <h1>Admin Log In</h1>
                <div className="id">
                    <input placeholder={"id"} value={id} onChange={IdChange} type={"text"}/><br/>
                </div>
                <div className="password">
                    <input type={"password"} id="password" placeholder="Password" value={password} onKeyDown={PasswordKeyDown} onChange={PasswordChange}/><br/>
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
            axios.delete('v1/admin/member', {params: {name: memberName}})
                .then((res)=>{
                    Swal.fire(memberName + "member delete ok");
                })
        }
        function AdminTokenClick(){
            axios.get(  'v1/auth/admin')
                .then((res) => {
                    window.location.href = res.data;
                    // alert("admin token ok");
                });
        }
        function InClusterClick(){
            axios.get('v1/admin/incluster').then((res)=>{
                Swal.fire("incluster ok");
            })
        }
        function HaneClick(){
            axios.get('v1/admin/hane').then((res)=>{
                Swal.fire("24Hane Token ok");
            })
        }
        function ImageClick(){
            axios.get('v1/admin/image').then((res)=>{
                Swal.fire("Image DB ok");
            })
        }
        function FlashClick(){
            axios.delete('v1/admin/flash').then((res)=>{
                Swal.fire("flash DB deleted");
            })
        }
        function LogoutClick() {
            axios.get('v1/admin/logout').then((res) => {
                Swal.fire("logout ok");
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