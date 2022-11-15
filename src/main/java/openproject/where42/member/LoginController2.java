//package openproject.where42.member;
//
//import lombok.RequiredArgsConstructor;
//import openproject.where42.api.ApiService;
//import openproject.where42.api.dto.Seoul42;
//import openproject.where42.check.AES;
//import openproject.where42.check.CheckApi;
//import openproject.where42.check.MakeCookie;
//import openproject.where42.exception.CookieExpiredException;
//import openproject.where42.exception.SessionExpiredException;
//import openproject.where42.exception.UnregisteredMemberException;
//import openproject.where42.member.domain.Member;
//import openproject.where42.response.Response;
//import openproject.where42.response.ResponseMsg;
//import openproject.where42.response.StatusCode;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.CookieValue;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;
//
//@RestController
//@RequiredArgsConstructor
//public class LoginController2 {
//    private final MemberRepository memberRepository;
//    private final CheckApi checkApi = new CheckApi();
//    static private MakeCookie oven = new MakeCookie();
//    private final AES aes = new AES();
//    static private ApiService apiService = new ApiService();
//
//    @GetMapping("/v1/home") // home 접속 시 세션 유무를 판단하여 세션이 && 토큰이 있으면 바로 메인화면 없을 경우 전부 42auth로 연결
//    public ResponseEntity home(@CookieValue(value = "access_token", required = false) String token, HttpServletRequest req) {
//        HttpSession session = req.getSession(false);
//        if (session != null && token != null) // refresh 관련은 성훈이가
//            return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.LOGIN_SUCCESS), HttpStatus.OK); // case A, 메인 화면으로 넘어가도록
//        return new ResponseEntity(Response.res(StatusCode.UNAUTHORIZED, ResponseMsg.LOGIN_FAIL), HttpStatus.UNAUTHORIZED); // case B ~ F. 에러 객체..?
//    }
//
//    @GetMapping("/v1/login")
//    public ResponseEntity login(@CookieValue(value = "access_token", required = false) String token, HttpServletRequest req) {
//        if (token == null)
//            throw new CookieExpiredException(); // case A ~ C, 쿠키 생성하게 보냄
//        ResponseEntity<String> response2 = checkApi.callMeInfo(); // v2/me 부르는 로직 // token 유효성 검증 apiservice에서 하기
//        Seoul42 seoul42 = apiService.seoul42Mapping(response2.getBody()); // 이름만 가져올 방법 없나..
//        Member member = memberRepository.findByName(seoul42.getLogin()); // 멤버 검사
//        if (member == null)
//            throw new UnregisteredMemberException(seoul42); // case E, 동의 화면으로 넘어가도록
//        HttpSession session = req.getSession();
//        session.setAttribute("id", member.getId()); // case F, 메인 화면으로 넘어가도록
//        return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.LOGIN_SUCCESS), HttpStatus.OK);
//    }
//
//    @GetMapping("/auth/login/callback") // 쿠키가 없을 경우 42api로 리다이렉트 시켜 권한 획득 후 이 주소로 콜백됨
//    public ResponseEntity loginCallback(@RequestParam("code") String code, HttpServletResponse res, HttpServletRequest req) {
//        checkApi.setting(code); //access_code setting
//        /*** 쿠키 등록 ***/
//        res.addCookie(oven.bakingCookie("access_token", aes.encoding(checkApi.getAccess_token()), 7200));
//        res.addCookie(oven.bakingCookie("refresh_token", aes.encoding(checkApi.getRefresh_token()), 1209600));
//        res.addCookie(oven.bakingMaxAge("1209600", 1209600));
//
//        HttpSession session = req.getSession(false);
//        // 이미 세션이 있을 경우 쿠키가 만들어지면 바로 로그인 처리 해주고
//        if (session != null) // case B
//            return new ResponseEntity<>(Response.res(StatusCode.OK, ResponseMsg.LOGIN_SUCCESS), HttpStatus.OK);
//
//        // 세션이 없다면 api 호출해서 멤버 여부 파악 후 동의 화면으로 보내거나(401 에러 with seoul42), 세션 생성 후 로그인 처리
//        ResponseEntity<String> response2 = checkApi.callMeInfo(); // v2/me 부르는 로직
//        // 이거 줄여야함
//        Seoul42 seoul42 = apiService.seoul42Mapping(response2.getBody());
//        if (!memberRepository.checkMemberByName(seoul42.getLogin())) // 멤버가 아니면(401 에러 및 api 획득 정보 함께 반환) 동의 화면으로 이동(프론트)
//            throw new UnregisteredMemberException(seoul42); // case C
//        Member member = memberRepository.findByName(seoul42.getLogin()); // id repository에서 바로 반환하는 게 난지 아님 거기도 걍 겟아이디인지
//        session = req.getSession();
//        session.setAttribute("id", member.getId()); // case D, 세션 등록
//        return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.LOGIN_SUCCESS), HttpStatus.OK); //로그인 처리
//    }
//
//    @GetMapping("/v1/logout")
//    public ResponseEntity logout(HttpServletRequest req) {
//        HttpSession session = req.getSession(false);
//        if (session == null)
//            throw new SessionExpiredException();
//        session.invalidate(); // 세션 삭제 해서 다시 로그인 하게끔. 이때 쿠키 자체는 살려둬도 될듯?
//        return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.LOGOUT_SUCCESS), HttpStatus.OK);
//    }
//}