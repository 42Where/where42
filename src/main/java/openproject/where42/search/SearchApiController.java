package openproject.where42.search;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import openproject.where42.member.domain.Member;
import openproject.where42.member.repository.MemberRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SearchApiController {

    private final MemberRepository memberRepository;

    @GetMapping("/42seoul") // 42cadet?
    public Search42UserResponse search42user(@RequestBody String searchWord) {
//        return new Search42UserResponse(Seoul42.search42seoul(searchWord));
        return null;
    }

    @Data
    static class Search42UserResponse {
        private List<Seoul42> matchUser;

        public Search42UserResponse(List<Seoul42> matchUser) {
            this.matchUser = matchUser;
        }
    }

    @GetMapping("/42seoul/{memberId}/user") // 주소 머라고 하냠...
    public Seoul42 getCadetInfo(@PathVariable Long memberId, @RequestBody Seoul42 cadet) {
        if (memberRepository.checkFriendByMemberIdAndName(memberId, cadet.getName())) // 이거를 선택하기 전에 할 지 말지임. 크게 시간은 안걸려서 걍 선택하기 전에 조회해줘도 크게 문제는 없겠다 싶음
            cadet.setFriend(true);
        Member member = memberRepository.findById(memberId);
        cadet.setMsg(member.getMsg());
        if (member != null) {
//            if (hane is 출근) {
//                cadet.setInOutState(1);
//                if (cadet.getLocate().getPlanet() == null) {
//                    cadet.setLocate(member.getLocate());
//                }
//            }
//            else {
//                cadet.setInOutState(0);
//                member.getLocate().updateLocate(null, -1, -1, null);
//                cadet.setLocate(member.getLocate());
//            }
        }
        return cadet;
    }
}
