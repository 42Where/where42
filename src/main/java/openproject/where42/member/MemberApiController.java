package openproject.where42.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberApiController {
    private final MemberService memberService;
    private final TokenService tokenService;

    @PostMapping(Define.WHERE42_VERSION_PATH + "/member")
    public ResponseEntity createMember(HttpSession session, @RequestBody Seoul42 seoul42) {
        Long memberId = memberService.saveMember(seoul42.getLogin(), seoul42.getImage().getLink(), seoul42.getLocation());
        session.setAttribute("id", memberId);
        session.setMaxInactiveInterval(60 * 60);
        return new ResponseEntity(ResponseWithData.res(StatusCode.CREATED, ResponseMsg.CREATE_MEMBER, memberId), HttpStatus.CREATED);
    }

    // 메인 정보 조회
    @GetMapping(Define.WHERE42_VERSION_PATH + "/member/member")
    public MemberInfo memberInformation(HttpServletRequest req, HttpServletResponse res, @CookieValue(value = "ID", required = false) String key) {
        String token42 = tokenService.getToken(res, key);
        Member member = memberService.findBySessionWithToken(req, token42);
        log.info("[member] \"{}\"님이 메인화면을 불렀습니다.", member.getName());
        if (member.timeDiff() < 1) {
            if (!Define.PARSED.equalsIgnoreCase(member.getLocation()))
                memberService.parseStatus(member, member.getLocate().getPlanet());
            return new MemberInfo(member);
        }
        memberService.parseStatus(member, token42);
        return new MemberInfo(member);
    }

    /*** 프론트랑 논의 필요 ***/
    @GetMapping(Define.WHERE42_VERSION_PATH + "/member/group") // 이 부분 프론트랑 논의 멤버 아이디를 보내주면 굳이 안찾아도 되니까
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
    public String getPersonalMsg(HttpServletRequest req, HttpServletResponse res, @CookieValue(value = "ID", required = false) String key) {
        String token42 = tokenService.getToken(res, key);
        Member member = memberService.findBySessionWithToken(req, token42);
        return member.getMsg();
    }

    @PostMapping(Define.WHERE42_VERSION_PATH + "/member/setting/msg") // 상태메시지 설정
    public ResponseEntity updatePersonalMsg(HttpServletRequest req,  HttpServletResponse res, @CookieValue(value = "ID", required = false) String key, @RequestBody Map<String, String> msg) {
        String token42 = tokenService.getToken(res, key);
        memberService.updatePersonalMsg(req, token42, msg.get("msg"));
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
    public ResponseEntity updateLocate(HttpServletRequest req, HttpServletResponse res, @CookieValue(value = "ID", required = false) String key, @RequestBody Locate locate) {
        String token42 = tokenService.getToken(res, key);
        Member member = memberService.findBySessionWithToken(req, token42);
        memberService.updateLocate(member, locate);
        return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.SET_LOCATE), HttpStatus.OK);
    }
}