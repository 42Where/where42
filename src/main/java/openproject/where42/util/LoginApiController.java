package openproject.where42.util;

import lombok.RequiredArgsConstructor;
import openproject.where42.exception.customException.CannotAccessAgreeException;
import openproject.where42.exception.customException.TokenExpiredException;
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
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * 로그인 관련 API 컨트롤러 클래스
 * @version 1.0
 */
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

    /**
     * 로그인 여부 조회
     * @param req 세션 확인용 HttpServletRequest
     * @param res 토큰 확인용 HttpServletResponse
     * @param key 토큰 확인용 쿠키값
     * @return 로그인 성공
     * @throws TokenExpiredException 쿠키에 토큰key가 저장되어 있지않거나, db에 저장된 토큰이 없는 경우 // 이거 성훈이 확인 필
     * @throws UnregisteredMemberException 등록되지 않은 멤버의 경우 401 예외 throw
     * @see TokenService#findAccessToken(HttpServletResponse, String) 토큰 조회
     * @see MemberService#findBySessionWithToken(HttpServletRequest, String) 세션 및 토큰으로 멤버 조회
     * @since 1.0
     * @author hyunjcho
     */
    @GetMapping(Define.WHERE42_VERSION_PATH + "/home")
    public ResponseEntity home(HttpServletRequest req, HttpServletResponse res, @CookieValue(value = "ID", required = false) String key) {
        token = tokenService.findAccessToken(res, key);
        memberService.findBySessionWithToken(req, token);
        return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.LOGIN_SUCCESS), HttpStatus.OK);
    }

    /**
     * <pre>
     *     로그인 되어있지 않고 세션도 존재하지 않는 유저가 로그인하려는 경우
     *     42api me정보 호출을 통해 멤버인지 확인 후 멤버인 경우 세션 생성, 아닌 경우 예외 throw
     *     다만 세션의 경우 멤버가 많아질수록 조회 속도가 느려지는 등의 이슈가 발생할 수 있어 jwt 토큰 방식으로 변경 고려를 추천함
     * </pre>
     * @param req 세션 확인용 HttpServletRequest
     * @param res 토큰 확인용 HttpServletResponse
     * @param key 토큰 확인용 쿠키값
     * @return 로그인 성공
     * @throws TokenExpiredException 쿠키에 토큰key가 저장되어 있지않거나, db에 저장된 토큰이 없는 경우 // 이거 성훈이 확인 필
     * @throws UnregisteredMemberException 등록되지 않은 멤버의 경우 401 예외 throw with seoul42 Data
     * @see TokenService#findAccessToken(HttpServletResponse, String) 토큰 조회
     * @see MemberService#findBySessionWithToken(HttpServletRequest, String) 세션 및 토큰으로 멤버 조회
     * @since 1.0
     * @author hyunjcho
     */
    @GetMapping(Define.WHERE42_VERSION_PATH + "/login")
    public ResponseEntity login(HttpServletRequest req, HttpServletResponse res, @CookieValue(value = "ID", required = false) String key) {
        token = tokenService.findAccessToken(res, key);
        Seoul42 seoul42 = apiService.getMeInfo(token);
        Member member = memberRepository.findByName(seoul42.getLogin());
        if (member == null)
            throw new UnregisteredMemberException(seoul42);
        session = req.getSession();
        session.setAttribute("id", member.getId());
        session.setMaxInactiveInterval(60 * 60);
        return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.LOGIN_SUCCESS), HttpStatus.OK);
    }

    /**
     * 동의 페이지 접근 가능 여부 조회
     * @param req 세션 확인용 HttpServletRequest
     * @param res 토큰 확인용 HttpServletResponse
     * @param key 토큰 확인용 쿠키값
     * @return 가입되지 않은 유저의 경우 동의 페이지 접근 가능 반환
     * @throws CannotAccessAgreeException 세션이 있거나, 이미 멤버인 경우
     * @since 1.0
     * @author hyunjcho
     */
    @GetMapping(Define.WHERE42_VERSION_PATH + "/checkAgree")
    public ResponseEntity checkAgree(HttpServletRequest req, HttpServletResponse res, @CookieValue(value = "ID", required = false) String key) {
        token = tokenService.findAccessToken(res, key);
        session = req.getSession(false);
        if (session != null)
            throw new CannotAccessAgreeException();
        Seoul42 seoul42 = apiService.getMeInfo(token);
        Member member = memberRepository.findByName(seoul42.getLogin());
        if (member != null) {
            session = req.getSession();
            session.setAttribute("id", member.getId());
            throw new CannotAccessAgreeException();
        }
        return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.UNREGISTERED), HttpStatus.OK);
    }

    /**
     * <pre>
     *     전달받은 code를 활용하여 42api Access Token 발급
     *     토큰 발급 후 세션이 있을 경우 바로 반환하며, 없을 경우 세션 생성 후 반환
     * </pre>
     * @param req 세션 확인용 HttpServletRequest
     * @param res 토큰 쿠키 저장용 HttpServletResponse
     * @param code 42api Access Token 발급을 위한 code
     * @return 로그인 성공
     * @throws UnregisteredMemberException 등록되지 않은 멤버의 경우 401 예외 throw with seoul42 Data
     * @since 1.0
     * @author hyunjcho
     */
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

    /**
     * 로그아웃(세션 및 토큰 쿠키 삭제)
     * @param req 세션 삭제용 HttpServletRequest
     * @param res 토큰 쿠키 삭제용 HttpServletResponse
     * @return 로그아웃 성공
     * @since 1.0
     * @author hyunjcho
     */
    @GetMapping(Define.WHERE42_VERSION_PATH + "/logout")
    public ResponseEntity logout(HttpServletRequest req, HttpServletResponse res) {
        res.addCookie(oven.burnCookie("ID"));
        res.addCookie(oven.bakingMaxAge("0", 0));
        session = req.getSession(false);
        if (session != null)
            session.invalidate();
        return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.LOGOUT_SUCCESS), HttpStatus.OK);
    }
}