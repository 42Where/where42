// import { useState } from 'react';
import { useMediaQuery } from 'react-responsive';
import './Agree_Desktop.css';
import './Agree_Mobile.css';

function Agree()
{
    const isMobile = useMediaQuery({ query: '(max-width: 930px'});
    const isDesktop = useMediaQuery({ query: '(min-width: 931px'});
    //api post : login, location, image_url (메인 호출하면 이정보가 오는데, 그걸 다시 보내줘야함)

    function Common(){
        const xmlhttp = new XMLHttpRequest();
        let   contents = null;

        xmlhttp.open("GET", "./wiki.txt", false);
        xmlhttp.overrideMimeType("text/html;charset=utf-8");
        xmlhttp.send();
        if (xmlhttp.status === 200) {
            contents = xmlhttp.responseText;
        }
        const AgreeClick=()=>{
            //axios api호출
        }
        return (
            <div id="Wrapper">
                <div id="HeaderWrapper">
                    <div id="AgreeHeader">개인 정보 이용 동의서</div>
                </div>
                <div id="AgreeContent">{contents}</div>
                <div id="AskWrapper">
                    <div id="AgreeAsk">개인정보 이용에 동의하시겠습니까?</div>
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