import axios from "axios";

const instance = axios.create({
    baseURL: '/v1/'
})

instance.interceptors.response.use(
        (res) => {
            return (res);
        }, (err) => {
            console.clear();
            if (err.response.status === 401 || err.response.status === 500)
                window.location.replace('/Login');
            else
                return Promise.reject(err);
        }
)

export default instance;