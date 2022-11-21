package openproject.where42.util;

import lombok.RequiredArgsConstructor;
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
    HttpSession session;

    @GetMapping("/v1/home")
    public ResponseEntity home(@CookieValue(value = "access_token", required = false) String token,
                               @CookieValue(value = "ID", required = false) String key,
                               HttpServletRequest req, HttpServletResponse res) {
        session = req.getSession(false);
        if (session != null && token != null)
            return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.LOGIN_SUCCESS), HttpStatus.OK); // case A, 메인 화면으로 넘어가도록
        if (session != null) {
            tokenService.inspectToken(res, key);
            return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.LOGIN_SUCCESS), HttpStatus.OK); // case A, 메인 화면으로 넘어가도록
        }
        throw new SessionExpiredException(); // case B ~ F, 로그인 화면으로 넘어가도록
    }

    @GetMapping("/v1/login")
    public ResponseEntity login(@CookieValue(value = "access_token", required = false) String token,
                                @CookieValue(value = "ID", required = false) String key,
                                HttpServletRequest req, HttpServletResponse res) {
        if (token == null){
            tokenService.checkRefreshToken(key); // 리프레시 토큰도 없을경우 case B ~ D, 42auth로 넘어가도록
            token = tokenService.issueAccessToken(key);
            tokenService.addCookie(res, token, key);
        }
        Seoul42 seoul42 = apiService.getMeInfo(token);
        Member member = memberRepository.findByName(seoul42.getLogin());
        if (member == null)
            throw new UnregisteredMemberException(seoul42); // case E, 동의 화면으로 넘어가도록
        session = req.getSession();
        session.setAttribute("id", member.getId()); // case F, 메인 화면으로 넘어가도록
        return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.LOGIN_SUCCESS), HttpStatus.OK);
    }

    @GetMapping("v1/auth/code")
    public ResponseEntity makeToken(@RequestParam("code") String code, HttpServletRequest req, HttpServletResponse res) {
        List<String> token = tokenService.beginningIssue(code);
        tokenService.addCookie(res, aes.encoding(token.get(1)), token.get(0));

        session = req.getSession(false);
        if (session != null) // case B
            return new ResponseEntity<>(Response.res(StatusCode.OK, ResponseMsg.LOGIN_SUCCESS), HttpStatus.OK);

        Seoul42 seoul42 = apiService.getMeInfo(aes.encoding(token.get(1)));
        Member member = memberRepository.findByName(seoul42.getLogin());
        if (member == null)
            throw new UnregisteredMemberException(seoul42); // case C, 동의 화면으로 넘어가도록
        session = req.getSession();
        session.setAttribute("id", member.getId()); // case D, 메인 화면으로 넘어가도록
        return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.LOGIN_SUCCESS), HttpStatus.OK);
    }

    @GetMapping("/v1/logout")
    public ResponseEntity logout(HttpServletRequest req) {
        session = req.getSession(false);
        if (session != null)
            session.invalidate();
        return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.LOGOUT_SUCCESS), HttpStatus.OK);
    }
}