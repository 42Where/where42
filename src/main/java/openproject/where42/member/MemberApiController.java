package openproject.where42.member;

import lombok.RequiredArgsConstructor;
import openproject.where42.member.dto.AdminInfo;
import openproject.where42.member.dto.MemberId;
import openproject.where42.util.Define;
import openproject.where42.exception.customException.*;
import openproject.where42.token.TokenService;
import openproject.where42.api.mapper.Seoul42;
import openproject.where42.groupFriend.GroupFriendDto;
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
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberApiController {
    private final MemberService memberService;
    private final TokenService tokenService;

    @PostMapping(Define.WHERE42_VERSION_PATH + "/member")
    public ResponseEntity createMember(HttpSession session, @RequestBody Seoul42 seoul42) {
        Long memberId = memberService.saveMember(seoul42.getLogin(), seoul42.getImage().getLink(), seoul42.getLocation());
        session.setAttribute("id", memberId);
        session.setMaxInactiveInterval(30 * 60); // 테스트 위해서 처음에 2분만 유지. 이후 디폴트 30분으로 하기 위해 삭제
        return new ResponseEntity(ResponseWithData.res(StatusCode.CREATED, ResponseMsg.CREATE_MEMBER, memberId), HttpStatus.CREATED);
    }

    // 메인 정보 조회
    @GetMapping(Define.WHERE42_VERSION_PATH + "/member/member")
    public MemberInfo memberInformation(HttpServletRequest req, HttpServletResponse res, @CookieValue(value = "ID", required = false) String key) {
        Member member = memberService.findBySession(req);
        if (member.timeDiff() < 1)
            return new MemberInfo(member);
        String token42 = tokenService.getToken(res, key);
        memberService.parseStatus(member, token42);
        return new MemberInfo(member);
    }

    @GetMapping(Define.WHERE42_VERSION_PATH + "/member/group")
    public List<MemberGroupInfo> memberGroupInformation(HttpServletRequest req) {
        Member member = memberService.findBySession(req);
        return memberService.findAllGroupFriendsInfo(member);
    }

    @GetMapping(Define.WHERE42_VERSION_PATH + "/member/friend")
    public List<GroupFriendDto> groupFriendsInformation(HttpServletRequest req) {
        Member member = memberService.findBySession(req);
        return memberService.findAllFriendsInfo(member);
    }

    @GetMapping(Define.WHERE42_VERSION_PATH + "/member/setting/msg") // 상태메시지 조회
    public String getPersonalMsg(HttpServletRequest req) {
        Member member = memberService.findBySession(req);
        return member.getMsg();
    }

    @PostMapping(Define.WHERE42_VERSION_PATH + "/member/setting/msg") // 상태메시지 설정
    public ResponseEntity updatePersonalMsg(HttpServletRequest req, @RequestBody String msg) {
        memberService.updatePersonalMsg(req, msg);
        return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.SET_MSG), HttpStatus.OK);
    }

    @GetMapping(Define.WHERE42_VERSION_PATH + "/member/setting/locate") // 위치 설정 가능 여부 조회
    public ResponseEntity checkLocate(HttpServletRequest req, HttpServletResponse res, @CookieValue(value = "ID", required = false) String key)
            throws OutStateException, TakenSeatException {
        String token42 = tokenService.getToken(res, key);
        int planet = memberService.checkLocate(req, token42);
        return new ResponseEntity(ResponseWithData.res(StatusCode.OK, ResponseMsg.NOT_TAKEN_SEAT, planet), HttpStatus.OK);
    }

    @PostMapping(Define.WHERE42_VERSION_PATH + "/member/setting/locate") // 위치 설정
    public ResponseEntity updateLocate(HttpServletRequest req, @RequestBody Locate locate) {
        Member member = memberService.findBySession(req);
        memberService.updateLocate(member, locate);
        return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.SET_LOCATE), HttpStatus.OK);
    }
}