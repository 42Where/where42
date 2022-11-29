import axios from 'axios';

export const DecideRoute = async (props) => {
    if (props.current === props.past)
        return

    let isLogin = false;
    await axios.get('/v1/home')
        .then(() => {
            isLogin = true;
        }).catch(() => {
            isLogin = false;
    });

    await console.log(props.past, isLogin);
    await check({current: props.current, past: props.past, isLogin: isLogin});
}

const check = (props) => {
    if (props.current === "/Login" && props.isLogin)
        window.location.replace("/Main");
    else if (props.current === "/Main" && !props.isLogin)
        window.location.replace("/Login");
    else if (props.current === "/Search" && !props.isLogin)
        window.location.replace("/Login");
    else if (props.current.match("/Setting")) {
        if (!props.isLogin)
            window.location.replace("/Login");
        else if (!(props.past === "/Main" || props.past.match("/Setting")))
            window.location.replace("/Main");
    }
    else if (props.current === "/Agree") {
        if (props.isLogin)
            window.location.replace("/Main");
        axios.get("/v1/checkAgree")
            .catch(() => {
                window.location.replace("/Login")
            });
    }
    else if (props.current === "/auth/login/callback") {
        if (props.isLogin)
            window.location.replace("/Main");
        else if (!(props.past === "Home" || props.past === "/Login"))
            window.location.replace("/Login");
    }
}