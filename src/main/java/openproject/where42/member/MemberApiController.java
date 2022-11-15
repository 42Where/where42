package openproject.where42.member;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import openproject.where42.api.dto.Seoul42;
import openproject.where42.group.GroupRepository;
import openproject.where42.group.GroupService;
import openproject.where42.group.domain.Groups;
import openproject.where42.groupFriend.GroupFriendDto;
import openproject.where42.member.domain.Locate;
import openproject.where42.groupFriend.GroupFriendRepository;
import openproject.where42.member.domain.Member;
import openproject.where42.member.dto.MemberAll;
import openproject.where42.member.dto.MemberGroupInfo;
import openproject.where42.member.dto.MemberInfo;
import openproject.where42.response.Response;
import openproject.where42.response.ResponseWithData;
import openproject.where42.response.ResponseMsg;
import openproject.where42.response.StatusCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;
    private final GroupService groupService;
    private final GroupRepository groupRepository;
    private final GroupFriendRepository groupFriendRepository;

    @PostMapping("/v1/member")
    public ResponseEntity createMember(HttpSession session, @RequestBody Seoul42 seoul42) {
        Long memberId = memberService.saveMember(seoul42.getLogin(), seoul42.getImage_url(), seoul42.getLocation());
        session.setAttribute("id", memberId);
        session.setMaxInactiveInterval(2 * 60); // 테스트 위해서 처음에 2분만 유지. 이후 디폴트 30분으로 하기 위해 삭제
        return new ResponseEntity(ResponseWithData.res(StatusCode.CREATED, ResponseMsg.CREATE_MEMBER, memberId), HttpStatus.CREATED);
    }

    // 메인 정보 조회
    @GetMapping("/v1/member")
    public ResponseMemberInfo memberInformation(HttpServletRequest req, @CookieValue("access_token") String token42) {
        Member member = memberService.findBySession(req);
        String tokenHane = "hanecookie";
        MemberInfo memberInfo = new MemberInfo(member, tokenHane, token42);
        if (memberInfo.isInitFlag())
            memberService.initLocate(member);
        List<MemberGroupInfo> groupList = memberService.findAllGroupFriendsInfo(member); // 그룹별 친구 오름차순 된거
        List<GroupFriendDto> groupFriendsList = memberService.findAllFriendsInfo(member, token42, tokenHane); // 해당 오너의 기본 그룹에 속한 친구들 정보 DTO로
        return new ResponseMemberInfo(memberInfo, groupList, groupFriendsList);
    }

    @Getter
    static class ResponseMemberInfo {
        MemberInfo memberInfo;
        List<MemberGroupInfo> groupInfo;
        List<GroupFriendDto> groupFriendsList;

        public ResponseMemberInfo(MemberInfo memberInfo, List<MemberGroupInfo> groupInfo, List<GroupFriendDto> groupFriendsList) {
            this.memberInfo = memberInfo;
            this.groupInfo = groupInfo;
            this.groupFriendsList = groupFriendsList;
        }
    }

    @GetMapping("/v1/member/setting/msg") // 상태메시지 조회
    public String getPersonalMsg(HttpServletRequest req) {
        Member member = memberService.findBySession(req);
        return member.getMsg();
    }
    @PostMapping("/v1/member/setting/msg") // 상태메시지 설정
    public ResponseEntity updatePersonalMsg(HttpServletRequest req, @RequestBody String msg) {
        memberService.updatePersonalMsg(req, msg);
        return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.SET_MSG), HttpStatus.OK);
    }

    @GetMapping("/v1/member/setting/locate") // 위치 설정 가능 여부 조회
    public ResponseEntity checkLocate(HttpServletRequest req, @CookieValue("access_token") String token42) {
        String tokenHane = "하네 토큰도 필요해용";
        memberService.checkLocate(req, tokenHane, token42);
        return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.NOT_TAKEN_SEAT), HttpStatus.OK);
    }

    @PostMapping("/v1/member/setting/locate") // 위치 설정
    public ResponseEntity updateLocate(HttpServletRequest req, @RequestBody Locate locate) {
        Member member = memberService.findBySession(req);
        memberService.updateLocate(member, locate);
        return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.SET_LOCATE), HttpStatus.OK);
    }

    @GetMapping("/v1/member/all") // 삭제할 메소드
    public MemberAll memberAll(HttpServletRequest req) {
        Member member = memberService.findBySession(req);
        return new MemberAll(member.getName(), member.getMsg(), member.getLocate(), groupService.findAllGroupsExceptDefault(member.getId()), groupFriendRepository.findGroupFriendsByGroupId(member.getDefaultGroupId()));
    }

    @GetMapping("/v1/member/allGroup") // 삭제할 메소드
    public List<Groups> groupList(HttpServletRequest req) {
        Member member = memberService.findBySession(req);
        List<Groups> groups = groupRepository.findGroupsByOwnerId(member.getId());
        return groups;
    }
}