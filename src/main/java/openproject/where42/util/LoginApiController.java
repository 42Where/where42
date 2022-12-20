package openproject.where42.util;

import lombok.RequiredArgsConstructor;
import openproject.where42.exception.customException.CannotAccessAgreeException;
import openproject.where42.exception.customException.TooManyRequestException;
import openproject.where42.member.MemberRepository;
import openproject.where42.token.TokenService;
import openproject.where42.api.ApiService;
import openproject.where42.api.mapper.Seoul42;
import openproject.where42.exception.customException.SessionExpiredException;
import openproject.where42.exception.customException.UnregisteredMemberException;
import openproject.where42.member.entity.Member;
import openproject.where42.util.response.Response;
import openproject.where42.util.response.ResponseMsg;
import openproject.where42.util.response.StatusCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
public class LoginApiController {
    private final MemberRepository memberRepository;
    private final TokenService tokenService;
    private final ApiService apiService;
    private HttpSession session;
    private String token;

    @GetMapping(Define.WHERE42_VERSION_PATH + "/home")
    public ResponseEntity home(@CookieValue(value = "ID", required = false) String key, HttpServletRequest req, HttpServletResponse res) {
        token = tokenService.findAccessToken(key);
        if (token == null)
            tokenService.inspectToken(res, key);
        session = req.getSession(false);
        if (session == null)
            throw new SessionExpiredException();
        return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.LOGIN_SUCCESS), HttpStatus.OK);
    }

    @GetMapping(Define.WHERE42_VERSION_PATH + "/login")
    public ResponseEntity login(@CookieValue(value = "ID", required = false) String key, HttpServletRequest req, HttpServletResponse res) {
        token = tokenService.findAccessToken(key);
        if (token == null){
            tokenService.checkRefreshToken(key);
            token = tokenService.issueAccessToken(key);
            tokenService.addCookie(res, key);
        }
        CompletableFuture<Seoul42> cf = apiService.getMeInfo(token);
        Seoul42 seoul42 = apiService.injectInfo(cf);
        Member member = memberRepository.findMember(seoul42.getLogin());
        if (member == null)
            throw new UnregisteredMemberException(seoul42);
        session = req.getSession();
        session.setAttribute("id", member.getId()); // 원래 있던거에 이렇게 넣어도 되나?
        return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.LOGIN_SUCCESS), HttpStatus.OK);
    }

    @Retryable(maxAttempts = 10, backoff = @Backoff(1000))
    @GetMapping(Define.WHERE42_VERSION_PATH + "/auth/login")
    public String authLogin() {
        return "";
    }

    @Recover
    public String fallback(RuntimeException e) {
        System.out.println("==== Login is doomed ====");
        e.printStackTrace();
        throw new TooManyRequestException();
    }

    @GetMapping(Define.WHERE42_VERSION_PATH + "/checkAgree")
    public ResponseEntity checkAgree(@CookieValue(value = "ID", required = false) String key, HttpServletRequest req, HttpServletResponse res) {
        token = tokenService.findAccessToken(key);
        if (token == null){
            tokenService.checkRefreshToken(key);
            token = tokenService.issueAccessToken(key);
            tokenService.addCookie(res, key);
        }
        session = req.getSession(false);
        if (session != null)
            throw new CannotAccessAgreeException();
        CompletableFuture<Seoul42> cf = apiService.getMeInfo(token);
        Seoul42 seoul42 = apiService.injectInfo(cf);
        Member member = memberRepository.findMember(seoul42.getLogin());
        if (member != null) {
            session = req.getSession();
            session.setAttribute("id", member.getId());
            throw new CannotAccessAgreeException();
        }
        return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.UNREGISTERED), HttpStatus.OK);
    }

    @GetMapping(Define.WHERE42_VERSION_PATH + "/auth/code")
    public ResponseEntity makeToken(@RequestParam("code") String code, HttpServletRequest req, HttpServletResponse res) {
        Seoul42 seoul42 = tokenService.beginningIssue(res, code);
        session = req.getSession(false);
        if (session != null)
            return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.LOGIN_SUCCESS), HttpStatus.OK);
        if (!Define.ACCESS.contains(seoul42.getLogin())) {
            System.out.println("정식 배포 기간이 아니므로 로그인 할 수 없습니다.");
            return new ResponseEntity(Response.res(StatusCode.CONFLICT, ResponseMsg.LOGIN_FAIL), HttpStatus.CONFLICT);
        }
        Member member = memberRepository.findMember(seoul42.getLogin());
        if (member == null)
            throw new UnregisteredMemberException(seoul42);
        session = req.getSession();
        session.setAttribute("id", member.getId());
        return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.LOGIN_SUCCESS), HttpStatus.OK);
    }

    @GetMapping(Define.WHERE42_VERSION_PATH + "/logout")
    public ResponseEntity logout(HttpServletRequest req) {
        session = req.getSession(false);
        if (session != null)
            session.invalidate();
        return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.LOGOUT_SUCCESS), HttpStatus.OK);
    }
}