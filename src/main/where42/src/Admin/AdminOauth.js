import Loading from "./Etc/Loading";
import axios from 'axios';

function AdminOauth() {
    let code = new URL(window.location.href).searchParams.get("code");
    axios.post('v1/auth/admin/token', null,{params : {code: code}})
        .then((response) => {
            alert("code 전송완료");
        });

    return (
        <Loading/>
    )
}

export default AdminOauth;