package openproject.where42.member;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import openproject.where42.group.GroupService;
import openproject.where42.member.domain.Member;
import openproject.where42.member.domain.enums.MemberLevel;
import openproject.where42.member.repository.MemberRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Controller
@RequiredArgsConstructor
public class LoginController {
    private final MemberService memberService;
    private final MemberRepository memberRepository;

    private final GroupService groupService;

    @GetMapping("/auth/logins")
    public String login() {
        return "auth/logins";
    }

    @GetMapping("/auth/login/callback")
    public String loginCallback(@RequestParam("code") String code, Model model) {
        RestTemplate rt = new RestTemplate(); //http 요청을 간단하게 해줄 수 있는 클래스
        //HttpHeader 오브젝트 생성
        HttpHeaders codeHeaders = new HttpHeaders();
        codeHeaders.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        //HttpBody 오브젝트 생성
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type","authorization_code");
        params.add("client_id","150e45a44fb1c8b17fe04470bdf8fabd56c1b9841d2fa951aadb4345f03008fe");
        params.add("client_secret", "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");
        params.add("code", code);
        params.add("redirect_uri","http://localhost:8080/auth/login/callback");

        //HttpHeader와 HttpBody를 하나의 오브젝트에 담기
        HttpEntity<MultiValueMap<String, String>> authTokenRequest =
                new HttpEntity<>(params, codeHeaders);

        //실제로 요청하기
        //Http 요청하기 - POST 방식으로 - 그리고 response 변수의 응답을 받음.
        ResponseEntity<String> response = rt.exchange(
                "https://api.intra.42.fr/oauth/token",
                HttpMethod.POST,
                authTokenRequest,
                String.class
        );

        //Gson Library, JSON SIMPLE LIBRARY, OBJECT MAPPER(Check)
        ObjectMapper objectMapper = new ObjectMapper();
        OAuthToken oauthToken = null;
        //Model과 다르게 되있으면 그리고 getter setter가 없으면 오류가 날 것이다.
        try {
            oauthToken = objectMapper.readValue(response.getBody(), OAuthToken.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        // v2/me 부르는 로직
        ResponseEntity<String> response2 = memberService.callMeInfo(oauthToken);

        Seoul42 seoul42 = null;
        //Model과 다르게 되있으면 그리고 getter setter가 없으면 오류가 날 것이다.
        try {
            seoul42 = objectMapper.readValue(response2.getBody(), Seoul42.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        if (seoul42.getLocation() == null)
            seoul42.setLocation("자리에 없음");
        if (!memberRepository.checkMemberByName(seoul42.getLogin())) {
            model.addAttribute("seoul42", seoul42);
            return "member/checkAgree"; // 동의하지 않을 경우는 front에서 처리하나?
        }
        Member member = memberRepository.findByName(seoul42.getLogin());
        model.addAttribute("member", member); // member dto 만들어서 반환할 수 있도록!
        return "member/iAm";
    }