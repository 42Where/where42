import axios from "axios";
import {useNavigate} from "react-router";
import Loading from "./Loading";

function Home() {
    const nav = useNavigate();
    axios.get('/v1/home')
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