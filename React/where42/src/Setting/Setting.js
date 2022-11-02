import { useMediaQuery } from 'react-responsive';
import { useLocation } from 'react-router';
import './Setting_Desktop.css';
import './Setting_Mobile.css';

function Setting() {
    const location = useLocation();
    const name = location.state?.name;
    const isMobile = useMediaQuery({ query: '(max-width: 930px'});
    const isDesktop = useMediaQuery({ query: '(min-width: 931px'});
    
    function Common() {
        return (
            <div id="Wrapper">
                <div id="Comment">반가워요, {name}! 👋</div>
                <div id="BoxWrapper">
                    <div id="SetLocate" className='Box'>
                        <div className='BoxCap'>
                            {isMobile && <>수동 위치 설정</>}
                            {isDesktop && <>수동<br/>위치 설정</>}
                        </div>
                    </div>
                    <div id="SetMsg" className='Box'>
                        <div className='BoxCap'>
                            {isMobile && <>상태 메시지 설정</>}
                            {isDesktop && <>상태 메시지<br/>설정</>}
                        </div>
                    </div>
                    <div id="SetGroup" className='Box'>
                        <div className='BoxCap'>그룹 설정</div>
                    </div>
                    <div id="Logout" className='Box'>
                        <div className='BoxCap'>로그아웃</div>
                    </div>
                </div>
            </div>
        )
    }

    return (
        <div id="Setting">
            {isMobile && <div id="Mobile"><Common/></div>}
            {isDesktop && <div id="Desktop"><Common/></div>}
        </div>
    )
}



export default Setting;