import {Link} from "react-router-dom";
import './NotFound.css';

const NotFound=()=>{
    return (
        <div id={"NotFound"}>
            <div id={"content"}>404 : NotFound</div>
            <Link to={"/Login"}>
                <div className={"question"}></div>
                <div className={"question"}></div>
                <div id={"character"}></div>
            </Link>
        </div>
    )
}

export default NotFound;
// 404:Not Found
// 우주인 양옆에 물음표 => 우주인이 버튼 (누르면 login 페이지)
// (커서 올리면 느낌표?)
// (백그라운드 url => hover 느낌표)
// 배경은 분홍색
