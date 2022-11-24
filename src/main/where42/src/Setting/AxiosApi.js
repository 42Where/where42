import {useNavigate} from "react-router";
import axios from "axios";

const instance = axios.create({
    baseURL: '/v1/'
})

instance.interceptors.response.use(
        (res) => {
            return (res);
        }, (err) => {
            console.clear();
            /*nav 이동 - 401, 500*/
            return (err);
        }
)

export default instance;