import {Link} from "react-router-dom";
import './NotFound.css';

const NotFound=()=>{
    return (
        <div id={"NotFound"}>
            <div id={"NotFoundContent"}>404 : NotFound</div>
            <div id={"Comment"}>아래의 우주인을 누르면<br/>로그인 화면으로 돌아갑니다!</div>
            <Link to={"/Login"} id="NotFoundCharacter" title="로그인 페이지로 이동"/>
        </div>
    )
}

export default NotFound;
