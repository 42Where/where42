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
        return (
            <div id="AgreeForm">
                <div id="AgreeContent"></div>
                <div id="AgreeSign">
                    동의하시겠습니까?
                    <input type="checkbox" id="AgreeCheck"></input>
                    <button id="FinishButton" type="submit"/>
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