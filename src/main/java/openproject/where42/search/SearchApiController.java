package openproject.where42.search;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import openproject.where42.groupFriend.GroupFriendService;
import openproject.where42.member.Seoul42;
import openproject.where42.member.domain.Member;
import openproject.where42.member.repository.MemberRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class SearchApiController {

    private final MemberRepository memberRepository;
    private final GroupFriendService groupFriendService;

    @GetMapping("/search/{memberId}")
    public Search42UserResponse search42UserResponse(@PathVariable("memberId") Long memberId, @RequestParam("begin") String begin, @RequestParam("token") String token) {
        RestTemplate rt = new RestTemplate(); //http 요청을 간단하게 해줄 수 있는 클래스
        //HttpHeader 오브젝트 생성
        ObjectMapper objectMapper = new ObjectMapper();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token); // 로그인한 애의 토큰과 다르면 안되려나..?
        headers.add("Content-type", "application/json;charset=utf-8");
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        HttpEntity<MultiValueMap<String, String>> request =
                new HttpEntity<>(params, headers);

        String end;
        char first = begin.charAt(0);
        char last = begin.charAt(begin.length() - 1);
        if (first != 'z' && last == 'z') // az 면 az ~ b 까지 b 한글자인 인 intra 는 없으니까?
            end = String.valueOf((char)((int)first + 1));
        else if (first == 'z' && last == 'z') // zz면 zz ~ zzz 까지 검색..? 어떻게 해야할 지 모르겟음 끝을 모름..ㅎ
            end = begin + "z";
        else // a000b면 a000b ~ a000c 인데 a000c가 포함되어있어성.. 거의 일치하는 게 없을 거 같긴한데...
            end = begin.substring(0, begin.length() - 1) + String.valueOf((char)((int)last + 1));;

        URI url = UriComponentsBuilder.newInstance()
                .scheme("https").host("api.intra.42.fr").path("/v2/campus/29/users/")
                .queryParam("sort", "login")
                .queryParam("range[login]", begin + "," + end)
                .queryParam("page[size]", "5") // 100개이상 어떻게 해야하는 지 모르겠음.. 그게 끝인지 알 방법이.. 어떻게 다시 호출하지 거기부터..?
                .build()
                .toUri();

        System.out.println(url);

        ResponseEntity<String> response = rt.exchange(
                url.toString(),
                HttpMethod.GET,
                request,
                String.class);

        List<Seoul42> cadetList = null;
        try {
            cadetList = Arrays.asList(objectMapper.readValue(response.getBody(), Seoul42[].class));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        List<SearchCadet> searchCadets = new ArrayList<SearchCadet>();

        for (Seoul42 cadet : cadetList) {
            URI url2 = UriComponentsBuilder.newInstance()
                    .scheme("https").host("api.intra.42.fr").path("/v2/users/" + cadet.getLogin())
                    .build()
                    .toUri();

            System.out.println(url2);

            ResponseEntity<String> response2 = rt.exchange(
                    url2.toString(),
                    HttpMethod.GET,
                    request,
                    String.class);

            System.out.println(response2.getBody());

            SearchCadet searchCadet;
            try {
                searchCadet = objectMapper.readValue(response2.getBody(), SearchCadet.class);
                System.out.println(memberRepository.checkFriendByMemberIdAndName(memberId, searchCadet.getLogin()) + " true/false " + memberId);
                if (memberRepository.checkFriendByMemberIdAndName(memberId, searchCadet.getLogin()))
                    searchCadet.setFriend(true);
                searchCadets.add(searchCadet);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

       return new Search42UserResponse(searchCadets);
    }


    @Data
    static class Search42UserResponse {
        private List<SearchCadet> matchUser;

        public Search42UserResponse(List<SearchCadet> matchUser) {
            this.matchUser = matchUser;
        }
    }

    @GetMapping("/search/{memberId}/select")
    public SearchCadet getCadetInfo(@PathVariable("memberId") Long memberId, @RequestBody SearchCadet cadet) {
        int test = 3;

        if (memberRepository.checkMemberByName(cadet.getLogin())) {
            Member object = memberRepository.findByName(cadet.getLogin());
            if (3 == test) { // hane 조회 후 in 일 경우 inoutstate도 바꿈
                if (cadet.getLocation() != null)
                    cadet.updateApiLocate(cadet.getLocation());
                else
                    cadet.updateSelfLocate(object.getLocate());
            }
            else { // hane 아웃일 경우
                cadet.updateApiLocate(null);
            }
            cadet.setMsg(object.getMsg());
        } else { // 선택 대상이 멤버가 아닌 경우
            if (cadet.getLocation() != null) {
                cadet.updateApiLocate(cadet.getLocation());
                cadet.setInOutState(1);
            } else {
                cadet.setInOutState(-1);
            }
        }
        return cadet;
    }

    @PostMapping("/search/{memberId}/{friendName}")
    public Boolean saveFriend(@PathVariable("memberId") Long memberId, @PathVariable("friendName") String friendName) {
        Long groupId = memberRepository.findById(memberId).getDefaultGroupId();
        Long gId = Long.valueOf(1);
        groupFriendService.saveGroupFriend(friendName, gId);

        return true;
    }
}