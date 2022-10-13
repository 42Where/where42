package openproject.where42.member;

import lombok.RequiredArgsConstructor;
import openproject.where42.groupFriend.GroupFriendService;
import openproject.where42.groupFriend.domain.GroupFriend;
import openproject.where42.member.domain.Member;
import openproject.where42.member.dto.MemberForm;
import openproject.where42.member.dto.MemberInfo;
import openproject.where42.member.repository.MemberRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberApiController {
    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final GroupFriendService groupFriendService;

    @GetMapping("/member/{memberId}")
    public ResponseMemberInfo memberInfomation(@PathVariable ("memberId") Long memberId) { // 자기 상태랑 친구 정보 싹다 반환해 주는거
        MemberInfo memberInfo = new MemberInfo();
//        GroupInfo groupInfo = new GroupInfo();
        Member member = memberRepository.findById(memberId); // repo 직접 접근하는데 서비스에 함수 만들어야 하나;
//        memberInfo = memberInfo.getMyInfo(member); // 이부분,, 뭐 팩토리로 만들어야 하나?? 어떻게 하는 게 맞는건지 모르겠음..
//        List<GroupInfo> memberGroupsInfo = groupInfo.getMyGroupInfo(memberId); // 그룹 오름차순 정리 및 그룹별 친구 이름 오름차순 정렬
//        List<GroupFriendInfo> groupFriendList = groupMemberService.getMyGroupMemberInfo(memberId); // 해당 오너의 기본 그룹에 속한 친구들 전부 반환.. friendList.. 하고픔..

//        return new ResponseMemberInfo(memberInfo, groupList, groupFriendList);
        return null;
    }

    private static class ResponseMemberInfo {
        MemberInfo memberInfo;
//        List<GroupInfo> groupInfo;
        List<GroupFriend> groupFriendList;

//        public ResponseMemberInfo(MemberInfo memberInfo, List<GroupInfo> groupInfo, List<GroupFriend> groupFriendList) {
//            this.memberInfo = memberInfo;
//            this.groupInfo = groupInfo;
//            this.groupFriendList = groupFriendList;
//        }
    }

    @GetMapping("/member/{memberId}/profile")
    public MemberForm memberProfile(@PathVariable("memberId") Long memberId) {
        Member member = memberRepository.findById(memberId);
        return new MemberForm(member.getMsg(), member.getPlanet(), member.getFloor(), member.getPlace());
    }
}
