package openproject.where42.util;

import lombok.RequiredArgsConstructor;
import openproject.where42.api.Define;
import openproject.where42.api.ClusterService;
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
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
public class SearchApiController {
    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final FlashDataService flashDataService;
    private final TokenService tokenService;
    private static final ApiService apiService = new ApiService();
    private static final ClusterService CLUSTER_SERVICE = new ClusterService();

    @GetMapping(Define.WHERE42_VERSION_PATH + "/incluster") // 서버 실행 시 자동 실행 방법..? 2주에 한 번 해줘야 하는 것들을 모아놓고 스케쥴러로 돌려도 좋고..
    public void findInClusterCadet(@CookieValue(value = "ID", required = false) String key, HttpServletResponse rep) { // 낮밤을 바꿀 것인지?
        String token42 = tokenService.findAccessToken(key); // 여기부터 세션까지 한 번에 함수로 어딘가 static으로 만들어서두자
        if (token42 == null)
            tokenService.inspectToken(rep, key);
        CLUSTER_SERVICE.updateAllOccupyingCadet(token42);
    }
    @GetMapping(Define.WHERE42_VERSION_PATH + "/search")
    public List<SearchCadet> search42UserResponse(HttpServletRequest req, HttpServletResponse rep, @RequestParam("begin") String begin,
                                                  @CookieValue(value = "ID", required = false) String key) {
        String token42 = tokenService.findAccessToken(key); // 여기부터 세션까지 한 번에 함수로 어딘가 static으로 만들어서두자
        if (token42 == null)
            tokenService.inspectToken(rep, key);
        HttpSession session = req.getSession(false);
        if (session == null)
            throw new SessionExpiredException();
        begin = begin.toLowerCase();
        CompletableFuture<List<Seoul42>> cf = apiService.get42UsersInfoInRange(token42, begin, getEnd(begin));
        List<Seoul42> searchList = apiService.injectInfo(cf);
        List<SearchCadet> searchCadetList = new ArrayList<SearchCadet>();
        for (Seoul42 cadet : searchList) {
            SearchCadet searchCadet = searchCadetInfo(cadet.getLogin());
            if (memberRepository.checkFriendByMemberIdAndName((Long) session.getAttribute("id"), searchCadet.getName()))
                searchCadet.setFriend(true);
            searchCadetList.add(searchCadet);
        }
       return searchCadetList;
    }

    @GetMapping(Define.WHERE42_VERSION_PATH + "/search/where42")
    public List<SearchCadet> searchWhere42Info() {
        return SearchCadet.where42();
    }

    public SearchCadet searchCadetInfo(String name) {
        Member member = memberRepository.findMember(name);
        if (member != null)
            return new SearchCadet(member);
        FlashData flash = flashDataService.findByName(name);
        if (flash != null)
            return new SearchCadet(flash);
        return new SearchCadet(name, "img db에서 찾아서 보내주기");
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

    @PostMapping(Define.WHERE42_VERSION_PATH + "/search/select")
    public SearchCadet getSelectCadetInfo(@RequestBody SearchCadet cadet) {
        if (!Define.PARSED.equalsIgnoreCase(cadet.getLocation())) {
            if (cadet.isMember()) {
                Member member = memberRepository.findMember(cadet.getName());
                memberService.parseStatus(member);
                cadet.updateStatus(member.getLocate(), member.getInOrOut());
            } else {
                FlashData flash = flashDataService.findByName(cadet.getName());
                flashDataService.parseStatus(flash);
                cadet.updateStatus(flash.getLocate(), flash.getInOrOut());
            }
        }
        return cadet;
    }
}