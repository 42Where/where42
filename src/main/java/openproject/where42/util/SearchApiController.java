package openproject.where42.util;

import lombok.RequiredArgsConstructor;
import openproject.where42.api.Define;
import openproject.where42.exception.customException.CookieExpiredException;
import openproject.where42.member.FlashDataService;
import openproject.where42.member.MemberService;
import openproject.where42.member.entity.FlashData;
import openproject.where42.member.entity.Member;
import openproject.where42.token.TokenService;
import openproject.where42.api.ApiService;
import openproject.where42.api.dto.SearchCadet;
import openproject.where42.api.dto.Seoul42;
import openproject.where42.exception.customException.SessionExpiredException;
import openproject.where42.member.MemberRepository;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class SearchApiController {
    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final FlashDataService flashDataService;
    private final TokenService tokenService;
    private static final ApiService api = new ApiService();

    @GetMapping(Define.versionPath + "/search")
    public List<SearchCadet> search42UserResponse(HttpServletRequest req, HttpServletResponse rep, @RequestParam("begin") String begin,
                                                  @CookieValue(value = "ID", required = false) String key) {
        String token42 = tokenService.findAccessToken(key);
        if (token42 == null)
            tokenService.inspectToken(rep, key);
        HttpSession session = req.getSession(false);
        if (session == null)
            throw new SessionExpiredException();
        begin = begin.toLowerCase();
        List<Seoul42> searchList = api.get42UsersInfoInRange(token42, begin, getEnd(begin));
        List<SearchCadet> searchCadetList = new ArrayList<SearchCadet>();
        for (Seoul42 cadet : searchList) {
            SearchCadet searchCadet = searchCadetInfo(cadet.getLogin(), token42);
            if (memberRepository.checkFriendByMemberIdAndName((Long) session.getAttribute("id"), searchCadet.getLogin()))
                searchCadet.setFriend(true);
            searchCadetList.add(searchCadet);
        }
       return searchCadetList;
    }

    public SearchCadet searchCadetInfo(String name, String token42) {
        SearchCadet searchCadet;
        Member member = memberRepository.findMember(name);
        if (member != null) {
            if (member.timeDiff() < 3)
                searchCadet = new SearchCadet(member);
            else {
                searchCadet = api.get42DetailInfo(token42, name);
                memberService.updateLocation(member, searchCadet.getLocation());
            }
            searchCadet.setMember(true);
            return searchCadet;
        }

        FlashData flash = flashDataService.findByName(name);
        if (flash != null && flash.timeDiff() < 3)
            return new SearchCadet(flash);
        searchCadet = api.get42DetailInfo(token42, name);
        if (flash != null)
            flashDataService.updateLocation(flash, searchCadet.getLocation());
        else
            flashDataService.createFlashData(name, searchCadet.getImage().getLink(), searchCadet.getLocation());
        return searchCadet;
    }

    private String getEnd(String begin) { // z를 여러개 넣는 거.. 뭐가 더 나을까?
        char first = begin.charAt(0);
        char last = begin.charAt(begin.length() - 1);
        if (first != 'z' && last == 'z')
            return String.valueOf((char)((int)first + 1));
        else if (first == 'z' && last == 'z') // zz면 zz ~ zzz 까지 검색..? 어떻게 해야할 지 모르겟음 끝을 모름..ㅎ
            return begin + "z";
        else // a000b면 a000b ~ a000c 인데 a000c가 포함되어있어성.. 거의 일치하는 게 없을 거 같긴한데...
            return begin.substring(0, begin.length() - 1) + (char)((int) last + 1);
    }

    @PostMapping(Define.versionPath + "/search/select")
    public SearchCadet getSelectCadetInfo(@RequestBody SearchCadet cadet) {
        if (!Define.PARSED.equalsIgnoreCase(cadet.getLocation())) {
            if (cadet.isMember()) {
                Member member = memberRepository.findMember(cadet.getLogin());
                memberService.parseStatus(member);
                cadet.updateStatus(member.getLocate(), member.getInOrOut());
            } else {
                FlashData flash = flashDataService.findByName(cadet.getLogin());
                flashDataService.parseStatus(flash);
                cadet.updateStatus(flash.getLocate(), flash.getInOrOut());
            }
        }
        return cadet;
    }
}