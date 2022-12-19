import axios from "axios";

const instance = axios.create({
    baseURL: '/v1/'
})

instance.interceptors.response.use(
        (res) => {
            return (res);
        }, (err) => {
            console.clear();
            if (err.response.status === 401 || err.response.status === 500 || err.response.status === 501)
                window.location.replace('/Login');
            else if (err.response.status === 429)
                alert("잠시 후 다시 시도해주세요.");
            else
                return Promise.reject(err);
        }
)

export default instance;