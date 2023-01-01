package openproject.where42.util;

import lombok.RequiredArgsConstructor;
import openproject.where42.background.ImageRepository;
import openproject.where42.flashData.FlashDataService;
import openproject.where42.member.MemberService;
import openproject.where42.flashData.FlashData;
import openproject.where42.member.entity.Member;
import openproject.where42.member.entity.enums.Planet;
import openproject.where42.token.TokenService;
import openproject.where42.api.ApiService;
import openproject.where42.api.mapper.Seoul42;
import openproject.where42.member.MemberRepository;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
public class SearchApiController {
    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final FlashDataService flashDataService;
    private final TokenService tokenService;
    private final ApiService apiService;
    private final ImageRepository imageRepository;

    @GetMapping(Define.WHERE42_VERSION_PATH + "/search")
    public List<SearchCadet> search42UserResponse(HttpServletRequest req, HttpServletResponse rep, @RequestParam("begin") String begin,
                                                  @CookieValue(value = "ID", required = false) String key) {
        String token42 = tokenService.findAccessToken(key);
        if (token42 == null)
            tokenService.inspectToken(rep, key);
        Member member = memberService.findBySessionWithToken(req, token42);
        begin = begin.toLowerCase();
        CompletableFuture<List<Seoul42>> cf = apiService.get42UsersInfoInRange(token42, begin, getEnd(begin));
        List<Seoul42> searchList = apiService.injectInfo(cf);
        List<SearchCadet> searchCadetList = new ArrayList<SearchCadet>();
        for (Seoul42 cadet : searchList) {
            SearchCadet searchCadet = searchCadetInfo(cadet.getLogin());
            if (memberRepository.checkFriendByMemberIdAndName(member.getId(), searchCadet.getName()))
                searchCadet.setFriend(true);
            searchCadetList.add(searchCadet);
        }
        return searchCadetList;
    }

    public SearchCadet searchCadetInfo(String name) {
        Member member = memberRepository.findByName(name);
        if (member != null)
            return new SearchCadet(member);
        FlashData flash = flashDataService.findByName(name);
        if (flash != null)
            return new SearchCadet(flash);
        return new SearchCadet(name, imageRepository.findByName(name));
    }

    private String getEnd(String begin) {
        char first = begin.charAt(0);
        char last = begin.charAt(begin.length() - 1);
        if (first != 'z' && last == 'z')
            return String.valueOf((char)((int)first + 1));
        else if (first == 'z' && last == 'z')
            return begin + "z";
        else
            return begin.substring(0, begin.length() - 1) + (char)((int) last + 1);
    }

    @PostMapping(Define.WHERE42_VERSION_PATH + "/search/select")
    public SearchCadet getSelectCadetInfo(@RequestBody SearchCadet cadet) {
        if (cadet.isMember()) {
            Member member = memberRepository.findByName(cadet.getName());
            Planet planet = memberService.checkMemberStatus(member);
            if (!Define.PARSED.equalsIgnoreCase(member.getLocation()))
                memberService.parseStatus(member, planet);
            cadet.updateStatus(member.getLocate(), member.getInOrOut());
        }
        else {
            if (!Define.PARSED.equalsIgnoreCase(cadet.getLocation())) {
                FlashData flash = flashDataService.findByName(cadet.getName());
                flashDataService.parseStatus(flash);
                cadet.updateStatus(flash.getLocate(), flash.getInOrOut());
            }
        }
        return cadet;
    }
}