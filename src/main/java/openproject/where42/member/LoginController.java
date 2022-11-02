package openproject.where42.member;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import openproject.where42.check.CheckApi;
import openproject.where42.member.domain.Member;
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

import java.awt.*;

@Controller
@RequiredArgsConstructor
public class LoginController {
    private final MemberRepository memberRepository;
    private final CheckApi checkApi = new CheckApi();

    @GetMapping("/auth/logins")
    public String login() {
        return "auth/logins";
    }

    @GetMapping("/auth/login/callback")
    public String loginCallback(@RequestParam("code") String code, Model model) {
        ObjectMapper objectMapper = new ObjectMapper();
        checkApi.setting(code);
        // v2/me 부르는 로직
        ResponseEntity<String> response2 = checkApi.callMeInfo();

        // 이거 줄여야함
        System.out.println(response2.getBody());
        Seoul42 seoul42 = null;
        try {
            seoul42 = objectMapper.readValue(response2.getBody(), Seoul42.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        if (seoul42.getLocation() == null){
            if (!memberRepository.checkMemberByName(seoul42.getLogin())) {
            }
        }

        //Model과 다르게 되있으면 그리고 getter setter가 없으면 오류가 날 것이다.
        if (!memberRepository.checkMemberByName(seoul42.getLogin())) {
            model.addAttribute("seoul42", seoul42);
            return "member/checkAgree"; // 동의하지 않을 경우는 front에서 처리하나?
        }
        Member member = memberRepository.findByName(seoul42.getLogin());
        model.addAttribute("member", member); // member dto 만들어서 반환할 수 있도록!
        return "member/iAm";
    }



//    @GetMapping("/items/{itemId}/edit")
//    public String updateItemForm(@PathVariable("itemId") Long itemId, Model model) {
//        Book item = (Book) itemService.findOne(itemId);
//        BookForm form = new BookForm();
//        form.setId(item.getId());
//        form.setName(item.getName());
//        form.setPrice(item.getPrice());
//        form.setStockQuantity(item.getStockQuantity());
//        form.setAuthor(item.getAuthor());
//        form.setIsbn(item.getIsbn());
//
//        model.addAttribute("form", form);
//        return "items/updateItemForm";
//    }
}