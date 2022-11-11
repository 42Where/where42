package openproject.where42.member;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import openproject.where42.api.dto.Seoul42;
import openproject.where42.group.GroupRepository;
import openproject.where42.group.GroupService;
import openproject.where42.group.domain.Groups;
import openproject.where42.groupFriend.GroupFriendInfoDto;
import openproject.where42.member.domain.Locate;
import openproject.where42.groupFriend.GroupFriendRepository;
import openproject.where42.member.domain.Member;
import openproject.where42.member.dto.MemberAll;
import openproject.where42.member.dto.MemberGroupInfo;
import openproject.where42.member.dto.MemberInfo;
import openproject.where42.response.ResponseDto;
import openproject.where42.response.ResponseMsg;
import openproject.where42.response.StatusCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final GroupService groupService;
    private final GroupRepository groupRepository;
    private final GroupFriendRepository groupFriendRepository;

    @PostMapping("/v1/member")
    public ResponseEntity createMember(@RequestBody Seoul42 seoul42) {
        Long memberId = memberService.saveMember(seoul42.getLogin(), seoul42.getImage_url(), seoul42.getLocation());

        return new ResponseEntity(ResponseDto.res(StatusCode.OK, ResponseMsg.CREATE_MEMBER, memberId), HttpStatus.OK); // 첫 로그인 시에는 멤버 정보만 넘겨준다. 어차피 친구가 없으니까.
    }

    @GetMapping("/v1/member/{memberId}") // 메인 정보 조회
    public ResponseMemberInfo memberInformation(@PathVariable ("memberId") Long memberId, @CookieValue("access_token") String token42) {
        Member member = memberRepository.findById(memberId);
        String tokenHane = "hanecookie";
        MemberInfo memberInfo = new MemberInfo(member, tokenHane, token42);
        if (memberInfo.isInitFlag())
            memberService.initLocate(member);
        List<MemberGroupInfo> groupList = memberService.findAllGroupFriendsInfo(member); // 그룹별 친구 오름차순 된거
        List<GroupFriendInfoDto> groupFriendsList = memberService.findAllFriendsInfo(member, token42, tokenHane); // 해당 오너의 기본 그룹에 속한 친구들 정보 DTO로
        return new ResponseMemberInfo(memberInfo, groupList, groupFriendsList);
    }

    @Getter
    static class ResponseMemberInfo {
        MemberInfo memberInfo;
        List<MemberGroupInfo> groupInfo;
        List<GroupFriendInfoDto> groupFriendsList;

        public ResponseMemberInfo(MemberInfo memberInfo, List<MemberGroupInfo> groupInfo, List<GroupFriendInfoDto> groupFriendsList) {
            this.memberInfo = memberInfo;
            this.groupInfo = groupInfo;
            this.groupFriendsList = groupFriendsList;
        }
    }

    @GetMapping("/v1/member/{memberId}/setting/msg") // 상태메시지 조회
    public String getPersonalMsg(@PathVariable("memberId") Long memberId) {
        Member member = memberRepository.findById(memberId);
        return member.getMsg();
    }
    @PostMapping("/v1/member/{memberId}/setting/msg") // 상태메시지 설정
    public ResponseEntity updatePersonalMsg(@PathVariable("memberId") Long memberId, @RequestBody String msg) {
        memberService.updatePersonalMsg(memberId, msg);
        return new ResponseEntity(ResponseDto.res(StatusCode.OK, ResponseMsg.SET_MSG), HttpStatus.OK);
    }

    @GetMapping("/v1/member/{memberId}/setting/locate") // 위치 설정 가능 여부 조회
    public ResponseEntity checkLocate(@PathVariable("memberId") Long memberId, @CookieValue("access_token") String token42) {
        String tokenHane = "하네 토큰도 필요해용";
        memberService.checkLocate(memberId, tokenHane, token42);
        return new ResponseEntity(ResponseDto.res(StatusCode.OK, ResponseMsg.NOT_TAKEN_SEAT), HttpStatus.OK);
    }

    @PostMapping("/v1/member/{memberId}/setting/locate") // 위치 설정
    public ResponseEntity updateLocate(@PathVariable("memberId") Long memberId, @RequestBody Locate locate) {
        Member member = memberRepository.findById(memberId);
        memberService.updateLocate(member, locate);
        return new ResponseEntity(ResponseDto.res(StatusCode.OK, ResponseMsg.SET_LOCATE), HttpStatus.OK);
    }

    @GetMapping("/v1/member/{memberId}/all") // 삭제할 메소드
    public MemberAll memberAll(@PathVariable("memberId") Long memberId) {
        Member member = memberRepository.findById(memberId);
        return new MemberAll(member.getName(), member.getMsg(), member.getLocate(), groupService.findAllGroupsExceptDefault(memberId), groupFriendRepository.findGroupFriendsByGroupId(member.getDefaultGroupId()));
    }

    @GetMapping("/v1/member/{id}/allGroup") // 삭제할 메소드
    public List<Groups> groupList(@PathVariable("id") Long id) {
        List<Groups> groups = groupRepository.findGroupsByOwnerId(id);
        return groups;
    }
}