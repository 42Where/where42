import axios from "axios";
import * as Util from './Util';

const instance = axios.create({
    baseURL: '/v2/'
})

instance.interceptors.response.use(
        (res) => {
            return (res);
        }, (err) => {
            console.clear();
            if (err?.response?.status === 401 || err?.response?.status === 500 || err?.response?.status === 501)
                window.location.replace('/Login');
            else if (err?.response?.status === 429)
                Util.Alert("잠시 후 다시 시도해주세요.");
            else
                return Promise.reject(err);
        }
)

export {instance};
