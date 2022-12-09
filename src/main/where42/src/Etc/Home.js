import instance from "../AxiosApi";
import {useNavigate} from "react-router";
import Loading from "./Loading";

function Home() {
    const nav = useNavigate();
    instance.get('home')
        .then(() => {
            nav('/Main');
        });

    return (<Loading/>)
}

export default Home;