package openproject.where42.util;

import lombok.RequiredArgsConstructor;
import openproject.where42.exception.customException.CannotAccessAgreeException;
import openproject.where42.exception.customException.TooManyRequestException;
import openproject.where42.member.MemberRepository;
import openproject.where42.member.MemberService;
import openproject.where42.token.MakeCookie;
import openproject.where42.token.TokenService;
import openproject.where42.api.ApiService;
import openproject.where42.api.mapper.Seoul42;
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
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
public class LoginApiController {
    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final TokenService tokenService;
    private final ApiService apiService;
    private final MakeCookie oven = new MakeCookie();
    private HttpSession session;
    private String token;

    @GetMapping(Define.WHERE42_VERSION_PATH + "/home")
    public ResponseEntity home(HttpServletRequest req, HttpServletResponse res, @CookieValue(value = "ID", required = false) String key) {
        token = tokenService.findAccessToken(key);
        if (token == null)
            tokenService.inspectToken(res, key);
        memberService.findBySessionWithToken(req, token);
        return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.LOGIN_SUCCESS), HttpStatus.OK);
    }

    @GetMapping(Define.WHERE42_VERSION_PATH + "/login")
    public ResponseEntity login(HttpServletRequest req, HttpServletResponse res, @CookieValue(value = "ID", required = false) String key) {
        token = tokenService.findAccessToken(key);
        if (token == null){
            tokenService.checkRefreshToken(key);
            token = tokenService.issueAccessToken(key);
            tokenService.addCookie(res, key);
        }
        CompletableFuture<Seoul42> cf = apiService.getMeInfo(token);
        Seoul42 seoul42 = apiService.injectInfo(cf);
        Member member = memberRepository.findByName(seoul42.getLogin());
        if (member == null)
            throw new UnregisteredMemberException(seoul42);
        session = req.getSession();
        session.setAttribute("id", member.getId());
        session.setMaxInactiveInterval(60 * 60);
        return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.LOGIN_SUCCESS), HttpStatus.OK);
    }

    @Retryable(maxAttempts = 3, backoff = @Backoff(1000))
    @GetMapping(Define.WHERE42_VERSION_PATH + "/auth/login")
    public String authLogin() {
        return "";
    }

    @Recover
    public String fallback(RuntimeException e) {
        throw new TooManyRequestException();
    }

    @GetMapping(Define.WHERE42_VERSION_PATH + "/checkAgree")
    public ResponseEntity checkAgree(HttpServletRequest req, HttpServletResponse res, @CookieValue(value = "ID", required = false) String key) {
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
        Member member = memberRepository.findByName(seoul42.getLogin());
        if (member != null) {
            session = req.getSession();
            session.setAttribute("id", member.getId());
            throw new CannotAccessAgreeException();
        }
        return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.UNREGISTERED), HttpStatus.OK);
    }

    @PostMapping(Define.WHERE42_VERSION_PATH + "/auth/token")
    public ResponseEntity makeToken(HttpServletRequest req, HttpServletResponse res, @RequestBody Map<String, String> code) {
        Seoul42 seoul42 = tokenService.beginningIssue(res, code.get("code"));
        session = req.getSession(false);
        if (session != null) {
            if (memberService.findBySession(req) != null)
                return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.LOGIN_SUCCESS), HttpStatus.OK);
        }
        Member member = memberRepository.findByName(seoul42.getLogin());
        if (member == null)
            throw new UnregisteredMemberException(seoul42);
        session = req.getSession();
        session.setAttribute("id", member.getId());
        return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.LOGIN_SUCCESS), HttpStatus.OK);
    }

    @GetMapping(Define.WHERE42_VERSION_PATH + "/logout")
    public ResponseEntity logout(HttpServletRequest req, HttpServletResponse rep) {
        rep.addCookie(oven.burnCookie("ID"));
        rep.addCookie(oven.bakingMaxAge("0", 0));
        session = req.getSession(false);
        if (session != null)
            session.invalidate();
        return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.LOGOUT_SUCCESS), HttpStatus.OK);
    }
}