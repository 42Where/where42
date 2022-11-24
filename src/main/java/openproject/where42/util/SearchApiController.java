package openproject.where42.util;

import lombok.RequiredArgsConstructor;
import openproject.where42.api.dto.Define;
import openproject.where42.member.FlashDataService;
import openproject.where42.member.MemberService;
import openproject.where42.member.entity.FlashMember;
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
    private final ApiService api;
    private final TokenService tokenService;

    @GetMapping(Define.versionPath + "/search")
    public List<SearchCadet> search42UserResponse(HttpServletRequest req, HttpServletResponse rep,
                                                  @RequestParam("begin") String begin,
                                                  @CookieValue(value = "ID", required = false) String key) {
        HttpSession session = req.getSession(false); // 이거 어디 유틸로 뺄 수 있음 뺴자
        String token42 = tokenService.findAccessToken(key);
        if (session == null)
            throw new SessionExpiredException();
        if (token42 == null)
            tokenService.inspectToken(rep, key);
        begin = begin.toLowerCase();
        List<Seoul42> searchList = api.get42UsersInfoInRange(token42, begin, getEnd(begin));
        List<SearchCadet> searchCadetList = new ArrayList<SearchCadet>();

        for (Seoul42 cadet : searchList) {
            SearchCadet searchCadet = searchCadetInfo(cadet.getLogin(), token42);
            if (searchCadet != null) { // json e 처리?!
                if (memberRepository.checkFriendByMemberIdAndName((Long) session.getAttribute("id"), searchCadet.getLogin()))
                    searchCadet.setFriend(true);
                searchCadetList.add(searchCadet);
            }
        }
       return searchCadetList;
    }

    public SearchCadet searchCadetInfo(String name, String token42) {
        SearchCadet searchCadet;

        Member member = memberRepository.findMember(name);
        if (member != null) {
            if (member.timeDiff() < 4)
                searchCadet = new SearchCadet(member);
            else {
                searchCadet = api.get42DetailInfo(token42, name);
                member.updateLocation(searchCadet.getLocation());
                searchCadet.setMember(true);
            }
            searchCadet.setMember(true);
            return searchCadet;
        }

        FlashMember flash = flashDataService.findByName(name);
        if (flash != null && flash.timeDiff() < 4)
            return new SearchCadet(flash);
        searchCadet = api.get42DetailInfo(token42, name);
        if (flash != null)
            flash.updateLocation(searchCadet.getLocation());
        else
            flashDataService.createFlashData(name, searchCadet.getImage(), searchCadet.getLocation());
        return searchCadet;
    }

    private String getEnd(String begin) { // z를 여러개 넣는 거.. 뭐가 더 나을까?
        char first = begin.charAt(0); // abc - abd
        char last = begin.charAt(begin.length() - 1);
        if (first != 'z' && last == 'z') // az 면 az ~ b 까지 b 한글자인 인 intra 는 없으니까?
            return String.valueOf((char)((int)first + 1));
        else if (first == 'z' && last == 'z') // zz면 zz ~ zzz 까지 검색..? 어떻게 해야할 지 모르겟음 끝을 모름..ㅎ
            return begin + "z";
        else // a000b면 a000b ~ a000c 인데 a000c가 포함되어있어성.. 거의 일치하는 게 없을 거 같긴한데...
            return begin.substring(0, begin.length() - 1) + String.valueOf((char)((int)last + 1));
    }

    @PostMapping(Define.versionPath + "/search/select")
    public SearchCadet getSelectCadetInfo(@RequestBody SearchCadet cadet) {
        if (!cadet.isParsed())
            if (cadet.isMember()) {
                Member member = memberRepository.findMember(cadet.getLogin());
                memberService.parseStatus(member);
                cadet.updateStatus(member.getLocate(), member.getInOrOut());
            } else {
                FlashMember flash = flashDataService.findByName(cadet.getLogin());
                flashDataService.parseStatus(flash);
                cadet.updateStatus(flash.getLocate(), flash.getInOrOut());
        }
        return cadet;
    }
}