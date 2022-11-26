package openproject.where42.member;

import lombok.RequiredArgsConstructor;
import openproject.where42.api.Define;
import openproject.where42.exception.customException.*;
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
    public ResponseEntity createMember(HttpSession session, @RequestBody Seoul42 seoul42) throws DefaultGroupNameException {
        Long memberId = memberService.saveMember(seoul42.getLogin(), seoul42.getImage().getLink(), seoul42.getLocation());
        session.setAttribute("id", memberId);
        session.setMaxInactiveInterval(30 * 60); // 테스트 위해서 처음에 2분만 유지. 이후 디폴트 30분으로 하기 위해 삭제
        return new ResponseEntity(ResponseWithData.res(StatusCode.CREATED, ResponseMsg.CREATE_MEMBER, memberId), HttpStatus.CREATED);
    }

    // 메인 정보 조회
    @GetMapping(Define.versionPath + "/member/member")
    public MemberInfo memberInformation(HttpServletRequest req, HttpServletResponse res, @CookieValue(value = "ID", required = false) String key)
            throws CookieExpiredException, SessionExpiredException {
        Member member = memberService.findBySession(req);
        String token42 = tokenService.findAccessToken(key);
        if (token42 == null)
            tokenService.inspectToken(res, key);
        memberService.parseStatus(member, token42);
        return new MemberInfo(member);
    }

    @GetMapping(Define.versionPath + "/member/group")
    public List<MemberGroupInfo> memberGroupInformation(HttpServletRequest req) throws SessionExpiredException {
        Member member = memberService.findBySession(req);
        return memberService.findAllGroupFriendsInfo(member);
    }

    @GetMapping(Define.versionPath + "/member/friend")
    public List<GroupFriendDto> groupFriendsInformation(HttpServletRequest req, HttpServletResponse res, @CookieValue(value = "ID", required = false) String key)
            throws CookieExpiredException, SessionExpiredException {
        Member member = memberService.findBySession(req);
        String token42 = tokenService.findAccessToken(key);
        if (token42 == null)
            tokenService.inspectToken(res, key);
        return memberService.findAllFriendsInfo(member, token42);
    }

    @GetMapping(Define.versionPath + "/member/setting/msg") // 상태메시지 조회
    public String getPersonalMsg(HttpServletRequest req) throws SessionExpiredException {
        Member member = memberService.findBySession(req);
        return member.getMsg();
    }

    @PostMapping(Define.versionPath + "/member/setting/msg") // 상태메시지 설정
    public ResponseEntity updatePersonalMsg(HttpServletRequest req, @RequestBody String msg) throws SessionExpiredException {
        memberService.updatePersonalMsg(req, msg);
        return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.SET_MSG), HttpStatus.OK);
    }

    @GetMapping(Define.versionPath + "/member/setting/locate") // 위치 설정 가능 여부 조회
    public ResponseEntity checkLocate(HttpServletRequest req, HttpServletResponse rep, @CookieValue(value = "ID", required = false) String key)
            throws CookieExpiredException, SessionExpiredException, OutStateException, TakenSeatException {
        String token42 = tokenService.findAccessToken(key);
        if (token42 == null)
            tokenService.inspectToken(rep, key);
        int planet = memberService.checkLocate(req, token42);
        return new ResponseEntity(ResponseWithData.res(StatusCode.OK, ResponseMsg.NOT_TAKEN_SEAT, planet), HttpStatus.OK);
    }

    @PostMapping(Define.versionPath + "/member/setting/locate") // 위치 설정
    public ResponseEntity updateLocate(HttpServletRequest req, @RequestBody Locate locate) throws SessionExpiredException {
        Member member = memberService.findBySession(req);
        memberService.updateLocate(member, locate);
        return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.SET_LOCATE), HttpStatus.OK);
    }
}