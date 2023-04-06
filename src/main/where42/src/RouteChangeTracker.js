import {useLocation} from "react-router";
import {useEffect, useState} from "react";
import ReactGA from "react-ga";

const RouteChangeTracker = () => {
    const loc = useLocation();
    const [init, setInit] = useState(false);
    const TRACKING_ID = process.env.REACT_APP_GOOGLE_ANALYTICS_TRACKING_ID;

    useEffect(() => {
        if (!window.location.href.includes('localhost')) {
            ReactGA.initialize(TRACKING_ID);
        }
        setInit(true);
    }, []);

    useEffect(() => {
        if (init) {
            ReactGA.pageview(loc.pathname);
        }
    }, [init, loc]);
}

export default RouteChangeTracker;