package openproject.where42.util;

import lombok.RequiredArgsConstructor;
import openproject.where42.background.ImageRepository;
import openproject.where42.flashData.FlashDataService;
import openproject.where42.member.MemberService;
import openproject.where42.flashData.FlashData;
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
    private final ApiService apiService;
    private final ImageRepository imageRepository;

    @GetMapping(Define.WHERE42_VERSION_PATH + "/search")
    public List<SearchCadet> search42UserResponse(HttpServletRequest req, HttpServletResponse rep, @RequestParam("begin") String begin,
                                                  @CookieValue(value = "ID", required = false) String key) {
        String token42 = tokenService.findAccessToken(key); // 여기부터 세션까지 한 번에 함수로 어딘가 static으로 만들어서두자
        if (token42 == null)
            tokenService.inspectToken(rep, key);
        HttpSession session = req.getSession(false);
        if (session == null)
            throw new SessionExpiredException();
//        int i = 0;
//        while(true) {
//            CompletableFuture<List<Cluster>> cf = apiService.get42ClusterInfo(token42, i);
//            System.out.println("i = " + i);
//            List<Cluster> clusterCadets = apiService.injectInfo(cf);
//            for (Cluster cadet : clusterCadets) {
//                Member member = memberRepository.findMember(cadet.getUser().getLogin());
//                if (member != null)
//                    memberService.updateLocation(member, cadet.getUser().getLocation());
//                else {
//                    FlashData flash = flashDataService.findByName(cadet.getUser().getLogin());
//                    if (flash != null)
//                        flashDataService.updateLocation(flash, cadet.getUser().getLocation());
//                    else
//                        flashDataService.createFlashData(cadet.getUser().getLogin(), cadet.getUser().getImage().getLink(), cadet.getUser().getLocation());
//                }
//            }
//            for (Cluster cluster : clusterCadets) {
//                System.out.println("** name = " + cluster.getUser().getLogin() + " Image = " + cluster.getUser().getImage().getLink() + " location = " + cluster.getUser().getLocation() + " end_at = " + cluster.getEnd_at());
//            }
//            if (clusterCadets.get(49).getEnd_at() != null) //null로 할 수 있다면! 이거 조건 뺴도 됨!
//            {
//                break;
//            }
//            i++;
//        }
        begin = begin.toLowerCase();
        CompletableFuture<List<Seoul42>> cf = apiService.get42UsersInfoInRange(token42, begin, getEnd(begin));
        List<Seoul42> searchList = apiService.injectInfo(cf);
        List<SearchCadet> searchCadetList = new ArrayList<SearchCadet>();
        for (Seoul42 cadet : searchList) {
            SearchCadet searchCadet = searchCadetInfo(cadet.getLogin());
            if (memberRepository.checkFriendByMemberIdAndName((Long) session.getAttribute("id"), searchCadet.getLogin()))
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
        return new SearchCadet(name, imageRepository.findByName(name));
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