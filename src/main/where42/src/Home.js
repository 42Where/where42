import axios from "axios";
import {useNavigate} from "react-router";

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
                console.log(err);
            }
        });

    return (null)
}

export default Home;