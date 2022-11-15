import { useState } from 'react';
import { useMediaQuery } from 'react-responsive';
import './Agree_Desktop.css';
import './Agree_Mobile.css';

function Agree()
{
    const isMobile = useMediaQuery({ query: '(max-width: 930px'});
    const isDesktop = useMediaQuery({ query: '(min-width: 931px'});
    //api post : login, location, image_url (intraid만 보내주면 되는거 아닌가..?)

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