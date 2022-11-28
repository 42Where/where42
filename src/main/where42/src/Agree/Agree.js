import { useMediaQuery } from 'react-responsive';
import './Agree_Desktop.css';
import './Agree_Mobile.css';
import {useLocation, useNavigate} from "react-router";
import instance from "../AxiosApi";

function Agree()
{
    const isMobile = useMediaQuery({ query: '(max-width: 930px)'});
    const isDesktop = useMediaQuery({ query: '(min-width: 931px)'});

    function Common(){
        const location = useLocation();
        const nav = useNavigate();
        const info = location.state;
        const AgreeClick=()=>{
            const body = { login: info.login , image : info.image, location : info.location};
            instance.post('member', body)
                .then((response)=>{
                    nav("/Main")}
                );
        }
        return (
            <div id="Wrapper">
                <div id="HeaderWrapper">
                    <div id="AgreeHeader">어디있니 개인 정보 이용 동의서</div>
                </div>
                <div id="AgreeContent">
                    <br/>
                    (재)이노베이션 아카데미는 『개인정보 보호법』 제15조 등 관련 법령에 따라<br/> 서비스 이용자의 개인정보보호를 매우 중시하며, 서비스 제공에 반드시 필요한 <br/>개인정보의 수집⦁이용을
                    위하여 귀하의 동의를 받고자 합니다.
                    <br/>
                    <br/>
                    <table>
                        <tr>
                            <td>개인정보의 수집 및 이용 목적 </td>
                            <td className="underline bold">어디있니 현재 위치 확인 서비스 제공</td>
                        </tr>
                        <tr>
                            <td>수집하는 개인정보 항목 (필수)</td>
                            <td className="underline bold">인트라 로그인 아이디, 클러스터 출입 상태, <br/>입실 시 현재 입실 한 클러스터,<br/> 출입카드 마지막 태그 시간</td>
                        </tr>
                        <tr>
                            <td>개인정보의 보유 및 이용기간</td>
                            <td><span className="underline bold blue">3년</span><span className="bold"> (보유기간 경과 및 보유목적 달성 시 지체 없이 파기합니다)</span></td>
                        </tr>
                        <tr>
                            <td>동의 거부 권리 및 동의 거부에 따른 불이익 내용 또는 제한사항</td>
                            <td>귀하는 개인정보 수집 및 이용에 대해 동의를 거부할 권리가 있습니다. 필수항목에 대한 동의 거부 시 <span className="underline bold blue">어디있니 서비스 제공</span> 이 제한됨을 알려드립니다.</td>
                        </tr>
                    </table>
                    <br/>
                    <span className='header'> (재)이노베이션 아카데미 귀하 </span><br/><br/>
                </div>
                <div id="AskWrapper">
                    <div id="AgreeAsk">(필수 항목) 개인정보 수집 및 이용에 동의하시겠습니까?</div>
                    <button id="AgreeCheck" onClick={AgreeClick}>동의</button>
                </div>
            </div>
        )
    }

    return (
        <div id="Agree">
            {isMobile && <div id="Mobile"><Common/></div>}
            {isDesktop && <div id="Desktop"><Common/></div>}
        </div>
    ) 

}

export default Agree;