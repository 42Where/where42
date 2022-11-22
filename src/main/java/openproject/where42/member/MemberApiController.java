package openproject.where42.member;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import openproject.where42.api.dto.Define;
import openproject.where42.token.TokenService;
import openproject.where42.api.dto.Seoul42;
import openproject.where42.groupFriend.entity.GroupFriendDto;
import openproject.where42.member.entity.Locate;
import openproject.where42.member.entity.Member;
import openproject.where42.member.dto.MemberGroupInfo;
import openproject.where42.member.dto.MemberInfo;
import openproject.where42.util.response.Response;
import openproject.where42.util.response.ResponseWithData;
import openproject.where42.util.response.ResponseMsg;
import openproject.where42.util.response.StatusCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;
    private final TokenService tokenService;

    @PostMapping(Define.versionPath + "/member")
    public ResponseEntity createMember(HttpSession session, @RequestBody Seoul42 seoul42) {
        Long memberId = memberService.saveMember(seoul42.getLogin(), seoul42.getImage_url(), seoul42.getLocation());
        session.setAttribute("id", memberId);
        session.setMaxInactiveInterval(30 * 60); // 테스트 위해서 처음에 2분만 유지. 이후 디폴트 30분으로 하기 위해 삭제
        return new ResponseEntity(ResponseWithData.res(StatusCode.CREATED, ResponseMsg.CREATE_MEMBER, memberId), HttpStatus.CREATED);
    }

    // 메인 정보 조회
    @GetMapping(Define.versionPath + "/member")
    public ResponseMemberInfo memberInformation(HttpServletRequest req, HttpServletResponse rep, @CookieValue("ID") String key) {
        Member member = memberService.findBySession(req);
        String token42 = tokenService.findAccessToken(key);
        if (token42 == null)
            tokenService.inspectToken(rep, key);
        MemberInfo memberInfo = new MemberInfo(member, token42);
        if (memberInfo.isInitFlag())
            memberService.initLocate(member);
        List<MemberGroupInfo> groupList = memberService.findAllGroupFriendsInfo(member); // 그룹별 친구 오름차순 된거
        List<GroupFriendDto> groupFriendsList = memberService.findAllFriendsInfo(member, token42); // 해당 오너의 기본 그룹에 속한 친구들 정보 DTO로
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

    @GetMapping(Define.versionPath + "/member/setting/msg") // 상태메시지 조회
    public String getPersonalMsg(HttpServletRequest req) {
        Member member = memberService.findBySession(req);
        return member.getMsg();
    }

    @PostMapping(Define.versionPath + "/member/setting/msg") // 상태메시지 설정
    public ResponseEntity updatePersonalMsg(HttpServletRequest req, @RequestBody String msg) {
        memberService.updatePersonalMsg(req, msg);
        return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.SET_MSG), HttpStatus.OK);
    }

    @GetMapping(Define.versionPath + "/member/setting/locate") // 위치 설정 가능 여부 조회
    public ResponseEntity checkLocate(HttpServletRequest req, HttpServletResponse rep,
                                      @CookieValue("access_token") String token42,
                                      @CookieValue("ID") String key) {
        if (token42 == null)
            tokenService.inspectToken(rep, key);
        int inOrOut = memberService.checkLocate(req, token42);
        return new ResponseEntity(ResponseWithData.res(StatusCode.OK, ResponseMsg.NOT_TAKEN_SEAT, inOrOut), HttpStatus.OK);

    }

    @PostMapping(Define.versionPath + "/member/setting/locate") // 위치 설정
    public ResponseEntity updateLocate(HttpServletRequest req, @RequestBody Locate locate) {
        Member member = memberService.findBySession(req);
        memberService.updateLocate(member, locate);
        return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.SET_LOCATE), HttpStatus.OK);
    }
}