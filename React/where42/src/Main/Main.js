import React from 'react';
import { useMediaQuery } from 'react-responsive';
import './Main.css';
import './Main_Desktop.css';
import './Main_Mobile.css';

function Main() {
    const isMobile = useMediaQuery({ query: '(max-width: 930px'});
    const isDesktop = useMediaQuery({ query: '(min-width: 931px'});

    return (
        <React.Fragment>
            {isMobile && <div id="Mobile"><Common/></div>}
            {isDesktop && <div id="Desktop"><Common/></div>}
        </React.Fragment>
    )
}

function Common() {
    return (
        <div id="Wrapper">
            <button id="Burger">🔍</button>
            <div id="Logo">
                <img src="img/logo_simple.svg" alt="logo"></img>
            </div>
        </div>
    )
}

export default Main;
