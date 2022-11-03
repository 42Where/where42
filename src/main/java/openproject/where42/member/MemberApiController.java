package openproject.where42.member;

import lombok.RequiredArgsConstructor;
import openproject.where42.group.GroupService;
import openproject.where42.groupFriend.domain.GroupFriendInfo;
import openproject.where42.groupFriend.repository.GroupFriendRepository;
import openproject.where42.member.domain.Member;
import openproject.where42.member.dto.MemberAll;
import openproject.where42.member.dto.MemberGroupInfo;
import openproject.where42.member.dto.MemberProfile;
import openproject.where42.member.dto.MemberInfo;
import openproject.where42.member.repository.MemberRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberApiController {
    private final MemberRepository memberRepository;
    private final GroupService groupService;
    private final GroupFriendRepository groupFriendRepository;

//    @GetMapping("/member/{memberId}")
//    public ResponseMemberInfo memberInformation(@PathVariable ("memberId") Long memberId) { // 자기 상태랑 친구 정보 싹다 반환해 주는거
//        Member member = memberRepository.findById(memberId); // repo 직접 접근하는데 서비스에 함수 만들어야 하나;
//        MemberInfo memberInfo = new MemberInfo(member);
//        List<MemberGroupInfo> groupList = memberService.findAllGroupFriendsInfo(memberId); // 그룹별 친구 오름차순 된거
//        List<GroupFriendInfo> groupFriendsList = memberService.findAllFriendsInfo(memberId); // 해당 오너의 기본 그룹에 속한 친구들 정보 DTO로 정리 된 ㄱ
//
//        return new ResponseMemberInfo(memberInfo, groupList, groupFriendsList);
//    }

//    @GetMapping("member/{memberName}")
//    public String memberTest(@PathVariable ("memberName") String name) {
//        System.out.println("is it possible?");
//        return "member/iAm";
//    }

    private static class ResponseMemberInfo {
        MemberInfo memberInfo;
        List<MemberGroupInfo> groupInfo;
        List<GroupFriendInfo> groupFriendsList;

        public ResponseMemberInfo(MemberInfo memberInfo, List<MemberGroupInfo> groupInfo, List<GroupFriendInfo> groupFriendsList) {
            this.memberInfo = memberInfo;
            this.groupInfo = groupInfo;
            this.groupFriendsList = groupFriendsList;
        }
    }

    @GetMapping("/member/{memberId}/profile")
    public MemberProfile memberProfile(@PathVariable("memberId") Long memberId) {
        Member member = memberRepository.findById(memberId);
        return new MemberProfile(member.getMsg(), member.getLocate());
    }

    @GetMapping("/member/{memberId}/all")
    public MemberAll memberAll(@PathVariable("memberId") Long memberId) {
        Member member = memberRepository.findById(Long.valueOf(1));
        return new MemberAll(member.getName(), groupService.findGroups(Long.valueOf(1)), groupFriendRepository.findGroupFriendsByGroupId(Long.valueOf(1)));
    }
}