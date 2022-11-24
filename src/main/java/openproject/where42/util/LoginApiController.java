package openproject.where42.util;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import openproject.where42.api.dto.Define;
import openproject.where42.member.MemberRepository;
import openproject.where42.token.TokenService;
import openproject.where42.api.ApiService;
import openproject.where42.api.dto.Seoul42;
import openproject.where42.token.AES;
import openproject.where42.exception.customException.SessionExpiredException;
import openproject.where42.exception.customException.UnregisteredMemberException;
import openproject.where42.member.entity.Member;
import openproject.where42.util.response.Response;
import openproject.where42.util.response.ResponseMsg;
import openproject.where42.util.response.StatusCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class LoginApiController {
    private final MemberRepository memberRepository;
    private final TokenService tokenService;
    private static final AES aes = new AES();
    private static final ApiService apiService = new ApiService();
    private HttpSession session;

    @GetMapping(Define.versionPath + "/home")
    public ResponseEntity home(@CookieValue(value = "ID", required = false) String key,
                               HttpServletRequest req, HttpServletResponse res) {
        String token = tokenService.findAccessToken(key);
        session = req.getSession(false);
        if (session != null && token != null)
            return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.LOGIN_SUCCESS), HttpStatus.OK); // case A, 메인 화면으로 넘어가도록
        if (session != null) {
            tokenService.inspectToken(res, key);
            return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.LOGIN_SUCCESS), HttpStatus.OK); // case A, 메인 화면으로 넘어가도록
        }
        throw new SessionExpiredException(); // case B ~ F, 로그인 화면으로 넘어가도록
    }

    @GetMapping(Define.versionPath + "/login")
    public ResponseEntity login(@CookieValue(value = "ID", required = false) String key,
                                HttpServletRequest req, HttpServletResponse res) {
        String token = tokenService.findAccessToken(key);
        if (token == null){
            tokenService.checkRefreshToken(key); // 리프레시 토큰도 없을경우 case B ~ D, 42auth로 넘어가도록
            token = tokenService.issueAccessToken(key);
            tokenService.addCookie(res, key);
        }
        Seoul42 seoul42 = apiService.getMeInfo(token);
        Member member = memberRepository.findMember(seoul42.getLogin());
        if (member == null)
            throw new UnregisteredMemberException(seoul42); // case E, 동의 화면으로 넘어가도록
        session = req.getSession();
        session.setAttribute("id", member.getId()); // case F, 메인 화면으로 넘어가도록
        return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.LOGIN_SUCCESS), HttpStatus.OK);
    }

    @RateLimiter(name = "42apiRateLimiter")
    @Retry(name = "42apiRetry")
    @GetMapping(Define.versionPath + "/auth/login") // 실 어플리케이션 발급 시 오픈소스에는 가리고 올려야 함
    public String authLogin() {
        /*** 로컬용 ***/
        return "https://api.intra.42.fr/oauth/authorize?client_id=150e45a44fb1c8b17fe04470bdf8fabd56c1b9841d2fa951aadb4345f03008fe&redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Fauth%2Flogin%2Fcallback&response_type=code";
        /*** 서버용 ***/
//        return "https://api.intra.42.fr/oauth/authorize?client_id=u-s4t2ud-6d1e73793782a2c15be3c0d2d507e679adeed16e50deafcdb85af92e91c30bd0&redirect_uri=http%3A%2F%2F54.180.140.84%2Fauth%2Flogin%2Fcallback&response_type=code";
    }

    @GetMapping(Define.versionPath + "/auth/code")
    public ResponseEntity makeToken(@RequestParam("code") String code, HttpServletRequest req, HttpServletResponse res) {
        Seoul42 seoul42 = tokenService.beginningIssue(res, code);

        session = req.getSession(false);
        if (session != null) // case B
            return new ResponseEntity<>(Response.res(StatusCode.OK, ResponseMsg.LOGIN_SUCCESS), HttpStatus.OK);

//        Seoul42 seoul42 = apiService.getMeInfo(aes.encoding(token.get(1)));
        Member member = memberRepository.findMember(seoul42.getLogin());
        if (member == null)
            throw new UnregisteredMemberException(seoul42); // case C, 동의 화면으로 넘어가도록
        session = req.getSession();
        session.setAttribute("id", member.getId()); // case D, 메인 화면으로 넘어가도록
        return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.LOGIN_SUCCESS), HttpStatus.OK);
    }

    @GetMapping(Define.versionPath + "/logout")
    public ResponseEntity logout(HttpServletRequest req) {
        session = req.getSession(false);
        if (session != null)
            session.invalidate();
        return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.LOGOUT_SUCCESS), HttpStatus.OK);
    }
}