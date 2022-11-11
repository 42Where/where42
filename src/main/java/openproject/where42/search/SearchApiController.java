package openproject.where42.search;

import lombok.RequiredArgsConstructor;
import openproject.where42.api.ApiService;
import openproject.where42.api.Utils;
import openproject.where42.api.dto.SearchCadet;
import openproject.where42.api.dto.Seoul42;
import openproject.where42.member.MemberRepository;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class SearchApiController {

    private final MemberRepository memberRepository;
    private final ApiService api;

    @GetMapping("/v1/search/{memberId}")
    public List<SearchCadet> search42UserResponse(@PathVariable("memberId") Long memberId, @RequestParam("begin") String begin, @CookieValue("access_token") String token) {
        List<Seoul42> searchList = api.get42UsersInfoInRange(token, begin, getEnd(begin));
        List<SearchCadet> searchCadetList = new ArrayList<SearchCadet>();

        for (Seoul42 cadet : searchList) {
            SearchCadet searchCadet = api.get42DetailInfo(token, cadet);
            if (searchCadet != null) { // json e 처리?!
                if (memberRepository.checkFriendByMemberIdAndName(memberId, searchCadet.getLogin()))
                    searchCadet.setFriend(true);
                searchCadetList.add(searchCadet);
            }
        }
       return searchCadetList;
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

    @GetMapping("/v1/search/select")
    public SearchCadet getSelectCadetInfo(@RequestBody SearchCadet cadet, @CookieValue("access_token") String token) { // 이부분은 사실 하네 토큰 필요
        Utils parseInfo = new Utils(token, memberRepository.findByName(cadet.getLogin()), cadet.getLocation());
        cadet.setMsg(parseInfo.getMsg());
        cadet.setLocate(parseInfo.getLocate());
        cadet.setInOrOut(parseInfo.getInOrOut());
        return cadet;
    }
}