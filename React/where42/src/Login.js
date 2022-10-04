import './Login.css'

function Login() {
    function clickDown() {
        const button = document.getElementById('Login-button');
        button.style = "background-image: url('img/login_button_click.svg'); background-size: contain";
    }
    function clickUp() {
        const button = document.getElementById('Login-button');
        button.style = "background-image: url('img/login_button.svg')";
        window.location.href = 'https://profile.intra.42.fr/users/sojoo';
    }
    function contactClick() {
        window.open('https://github.com/5ganization', '_blank');
    }
    function wikiClick() {
        // #Main을 날리고 <div>를 올려서 텍스트로 사용방법 보여주기
        // 우리는 친구 자리 찾기 페이지다~
        // 로그인 하시면 메인에 친구 자리 뜨고~ 친구 검색해서 등록도 되고~
        // 그룹 설정도 된다~~ 출퇴근을 위한 개인정보가 필요하다~
        // 바로 가입하시라~~ 저희는 where42이다~
    }
    return (
        <div className="Login">
            <div id="Icon">
                <button id="contact" onClick={contactClick}></button>
                <button id="wiki" onClick={wikiClick}></button>
            </div>
            <div id="Main">
                <div id="Logo">
                    <img src="img/logo.svg" alt="logo"></img>
                </div>
                <div id="Character">
                    <img src="img/character.svg" alt="character"></img>
                </div>
                <button id="Login-button" onMouseDown={clickDown} onMouseUp={clickUp}></button>
            </div>
        </div>
    );
}

export default Login;