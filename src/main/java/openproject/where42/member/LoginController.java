package openproject.where42.member;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import openproject.where42.api.ApiService;
import openproject.where42.api.dto.Seoul42;
import openproject.where42.check.AES;
import openproject.where42.check.CheckApi;
import openproject.where42.check.CheckCookie;
import openproject.where42.check.MakeCookie;
import openproject.where42.member.domain.Member;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequiredArgsConstructor
public class LoginController {
    private final MemberRepository memberRepository;
    private final CheckApi checkApi = new CheckApi();
    static private MakeCookie oven = new MakeCookie();
    private final AES aes = new AES();

    @GetMapping("/auth/logins")
    public String login() {
        return "auth/logins";
    }

    @GetMapping("/auth/login/callback")
    public String loginCallback(@RequestParam("code") String code, Model model, HttpServletResponse response) {
        ObjectMapper objectMapper = new ObjectMapper();
        CheckCookie checkCookie = new CheckCookie();

        checkApi.setting(code); //access_code setting

        /*** 쿠키 등록 ***/
        response.addCookie(oven.bakingCookie("access_token", aes.encoding(checkApi.getAccess_token()), 7200));
        response.addCookie(oven.bakingCookie("access_token", aes.encoding(checkApi.getRefresh_token()), 1209600));
        response.addCookie(oven.bakingMaxAge("1209600", 1209600));

        ResponseEntity<String> response2 = checkApi.callMeInfo(); // v2/me 부르는 로직
        // 이거 줄여야함
        Seoul42 seoul42 = null;
        try {
            seoul42 = objectMapper.readValue(response2.getBody(), Seoul42.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        if (seoul42.getLocation() == null) {
            if (!memberRepository.checkMemberByName(seoul42.getLogin())) {
            }
        }

        //Model과 다르게 되있으면 그리고 getter setter가 없으면 오류가 날 것이다.
        if (!memberRepository.checkMemberByName(seoul42.getLogin())) {
            model.addAttribute("seoul42", seoul42);
            return "/member/checkAgree"; // 동의하지 않을 경우는 front에서 처리하나?
        }
        Member member = memberRepository.findByName(seoul42.getLogin());
        model.addAttribute("member", member); // member dto 만들어서 반환할 수 있도록!
        return "/main_test";
    }

    //암복호화 테스트용
    @GetMapping("/cookietest")
    public String t(@CookieValue("access_token") String token) {
        AES aes = new AES();
        System.out.println(checkApi.getAccess_token());
        System.out.println(aes.decoding(token));
        return "/main_test";
    }
}