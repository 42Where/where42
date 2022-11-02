package openproject.where42.search;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import openproject.where42.api.ApiService;
import openproject.where42.groupFriend.GroupFriendService;
import openproject.where42.member.Seoul42;
import openproject.where42.member.domain.Member;
import openproject.where42.member.repository.MemberRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class SearchApiController {

    private final MemberRepository memberRepository;
    private final GroupFriendService groupFriendService;
    private final ApiService api;
    private final ObjectMapper objectMapper;

    @GetMapping("/search/{memberId}")
    public Search42UserResponse search42UserResponse(@PathVariable("memberId") Long memberId, @RequestParam("begin") String begin, @RequestParam("token") String token) {
        HttpEntity<MultiValueMap<String, String>> req = api.req42ApiHeader(token); // 동일 헤더 사용
        ResponseEntity<String> res = api.resApi(req, api.req42ApiSearchUsersUri(begin, getEnd(begin)));

        List<Seoul42> searchList = null;
        try {
            searchList = Arrays.asList(objectMapper.readValue(res.getBody(), Seoul42[].class));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        List<SearchCadet> searchCadetList = new ArrayList<SearchCadet>();
        for (Seoul42 cadet : searchList) {
            ResponseEntity<String> res2 = api.resApi(req, api.req42ApiSearchOneUserUri(cadet.getLogin()));

            SearchCadet searchCadet;
            try {
                searchCadet = objectMapper.readValue(res2.getBody(), SearchCadet.class);
                if (memberRepository.checkFriendByMemberIdAndName(memberId, searchCadet.getLogin()))
                    searchCadet.setFriend(true); // 제대로 반영이 안되는 듯함..?
                searchCadetList.add(searchCadet);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
       return new Search42UserResponse(searchCadetList);
    }

    private String getEnd(String begin) {
        char first = begin.charAt(0); // abc - abd
        char last = begin.charAt(begin.length() - 1);
        if (first != 'z' && last == 'z') // az 면 az ~ b 까지 b 한글자인 인 intra 는 없으니까?
            return String.valueOf((char)((int)first + 1));
        else if (first == 'z' && last == 'z') // zz면 zz ~ zzz 까지 검색..? 어떻게 해야할 지 모르겟음 끝을 모름..ㅎ
            return begin + "z";
        else // a000b면 a000b ~ a000c 인데 a000c가 포함되어있어성.. 거의 일치하는 게 없을 거 같긴한데...
            return begin.substring(0, begin.length() - 1) + String.valueOf((char)((int)last + 1));
    }

    @Data
    static class Search42UserResponse {
        private List<SearchCadet> matchUser;
        public Search42UserResponse(List<SearchCadet> matchUser) {
            this.matchUser = matchUser;
        }
    }

    @GetMapping("/search/{memberId}/select")
    public SearchCadet getCadetInfo(@PathVariable("memberId") Long memberId, @RequestParam String token, @RequestBody SearchCadet cadet) {
        int test = 3;
        ResponseEntity<String> res = api.resApi(api.req42ApiHeader(token), api.reqHaneApiUri(cadet.getLogin())); // 헤더추가가 어떻게 되는거지.. reset 되나?

        try {
            cadet = objectMapper.readValue(res.getBody(), SearchCadet.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        if (memberRepository.checkMemberByName(cadet.getLogin())) {
            Member member = memberRepository.findByName(cadet.getLogin());
            if (3 == test) { // hane 조회 후 in 일 경우 inoutstate도 바꿈
                if (cadet.getLocation() != null)
                    cadet.updateApiLocate(cadet.getLocation());
                else
                    cadet.updateSelfLocate(member.getLocate());
                cadet.setInOutStates(1);
            }
            else { // hane 아웃일 경우
                cadet.updateApiLocate(null);
                cadet.setInOutStates(0); // 이거 하네 변수명에 따라서 inoutstates로 바꿀지 이거 다시 말해줘야함 프론트한테
            }
            cadet.setMsg(member.getMsg());
        } else { // 선택 대상이 멤버가 아닌 경우
            if (cadet.getLocation() != null) {
                cadet.updateApiLocate(cadet.getLocation());
                cadet.setInOutStates(1);
            } else {
                cadet.setInOutStates(2);
                cadet.updateApiLocate(null);
            }
        }
        return cadet;
    }

    @PostMapping("/search/{memberId}/{friendName}")
    public Boolean saveFriend(@PathVariable("memberId") Long memberId, @PathVariable("friendName") String friendName) {
        Long groupId = memberRepository.findById(memberId).getDefaultGroupId();
        Long gId = Long.valueOf(1); // defaultgroupid 제대로 나오면 없애면 됨
        groupFriendService.saveGroupFriend(friendName, gId);
        return true;
    }
}