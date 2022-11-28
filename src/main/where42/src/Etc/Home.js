import instance from "../AxiosApi";
import {useNavigate} from "react-router";
import Loading from "./Loading";

function Home() {
    const nav = useNavigate();
    instance.get('home')
        .then(() => {
            nav('/Main');
        }).catch((err) => {
            if (err.response.status === 401) {
                nav('/Login');
            }
            else {
                // console.error(err);
            }
        });

    return (<Loading/>)
}

export default Home;