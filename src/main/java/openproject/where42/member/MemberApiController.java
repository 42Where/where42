package openproject.where42.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openproject.where42.member.entity.enums.Planet;
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
import java.util.List;
import java.util.Map;

/**
 * 멤버 관련 API 컨트롤러 클래스
 * @version 2.0
 * @see openproject.where42.member
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberApiController {
    private final MemberService memberService;
    private final TokenService tokenService;

    /**
     * <pre>
     *      멤버 생성
     *      개인정보제공 동의한 사용자에 한해 42api 정보를 바탕으로 생성
     *      멤버 생성 시 멤버 id를 바탕으로 세션을 생성함
     *      ver2 업데이트 시 기수 구분용 created_at 추가
     * </pre>
     * @param session 멤버 세션 생성용 세션
     * @param seoul42 멤버 등록을 위한 정보를 가진 42api 매핑 클래스
     * @return 생성된 멤버 아이디 반환
     * @see openproject.where42.api.mapper.Seoul42
     * @see openproject.where42.member.MemberService#saveMember(String, String, String, String) 멤버 저장
     * @since 1.0
     * @author hyunjcho
     */
    @PostMapping(Define.WHERE42_VERSION_PATH + "/member")
    public ResponseEntity createMember(HttpSession session, @RequestBody Seoul42 seoul42) {
        Long memberId = memberService.saveMember(seoul42.getLogin(), seoul42.getImage().getLink(), seoul42.getLocation(), seoul42.getCreated_at());
        session.setAttribute("id", memberId);
        session.setMaxInactiveInterval(60 * 60);
        return new ResponseEntity(ResponseWithData.res(StatusCode.CREATED, ResponseMsg.CREATE_MEMBER, memberId), HttpStatus.CREATED);
    }

    /**
     * <pre>
     *      멤버 프로필 및 현재 위치 조회
     *      이전 멤버 위치 갱신이 1분 이내일 경우 hane 및 42api를 조회하지 않고 현재 정보 기반으로 반환
     *      이때 파싱되지 않은 location 정보를 가지고 있는 경우 파싱 후 반환
     *      1분이 지났을 경우 hane 및 42api를 새로 조회하여 위치 정보 갱신
     * </pre>
     * @param req 세션 확인용 HttpServletRequest
     * @param res 토큰 확인용 HttpServletResponse
     * @param key 토큰 확인용 쿠키값
     * @return 갱신 및 파싱이 필요한 경우 해당 정보를 모두 파싱한 후 DTO에 정보를 담아 반환
     * @throws TokenExpiredException 쿠키에 토큰key가 저장되어 있지않거나, db에 저장된 토큰이 없는 경우 // 이거 성훈이 확인 필
     * @throws UnregisteredMemberException 등록되지 않은 멤버의 경우 401 예외 throw
     * @see openproject.where42.token.TokenService#findAccessToken(HttpServletResponse, String) 토큰 조회
     * @see openproject.where42.member.MemberService#findBySessionWithToken(HttpServletRequest, String) 세션 및 토큰으로 멤버 조회
     * @see openproject.where42.member.MemberService#parseStatus(Member, Planet) 위치 정보 파싱
     * @see openproject.where42.member.dto.MemberInfo 멤버 DTO 클래스
     * @since 1.0
     * @author hyunjcho
     */
    @GetMapping(Define.WHERE42_VERSION_PATH + "/member/member")
    public MemberInfo memberInformation(HttpServletRequest req, HttpServletResponse res, @CookieValue(value = "ID", required = false) String key) {
        String token42 = tokenService.findAccessToken(res, key);
        Member member = memberService.findBySessionWithToken(req, token42);
        log.info("[main] \"{}\"님이 메인화면을 조회하였습니다.", member.getName());
        if (member.timeDiff() < 1) {
            if (!Define.PARSED.equalsIgnoreCase(member.getLocation()))
                memberService.parseStatus(member, member.getLocate().getPlanet());
            return new MemberInfo(member);
        }
        memberService.parseStatus(member, token42);
        return new MemberInfo(member);
    }

    /**
     * <pre>
     *      멤버 그룹 정보 조회
     *      memberInformation api 호출 후에만 사용해야 하며, 멤버에 대한 유효성검사 진행하지 않음
     * </pre>
     * @param id 조회하려는 멤버 id
     * @return 멤버가 가지고 있는 그룹을 이름순으로 정렬하여 해당 그룹에 포함된 친구들의 이름을 정렬 후 DTO에 담아 반환
     * @see #memberInformation(HttpServletRequest, HttpServletResponse, String)
     * @see openproject.where42.member.MemberService#findById(Long) 아이디로 멤버 조회
     * @see openproject.where42.member.MemberService#findAllGroupFriendsInfo(Member) 친구 정보 조회
     * @see openproject.where42.member.dto.MemberGroupInfo 멤버 그룹 정보 DTO 클래스
     * @since 1.0
     * @author hyunjcho
     */
    @GetMapping(Define.WHERE42_VERSION_PATH + "/member/group")
    public List<MemberGroupInfo> memberGroupInformation(@RequestParam Long id) {
        Member member = memberService.findById(id);
        return memberService.findAllGroupFriendsInfo(member);
    }

    /**
     * <pre>
     *      멤버 친구 정보 조회
     *      memberInformation api 호출 후에만 사용해야 하며, 멤버에 대한 유효성검사 진행하지 않음
     * </pre>
     * @param id 조회하려는 멤버 id
     * @return 멤버가 등록한 친구들의 정보를 모두 정리하여 이름순으로 정렬한 후 DTO에 담아 반환
     * @see #memberInformation(HttpServletRequest, HttpServletResponse, String)
     * @see openproject.where42.member.MemberService#findById(Long) 아이디로 멤버 조회
     * @see openproject.where42.member.MemberService#findAllFriendsInfo(Member) 친구 정보 조회
     * @see openproject.where42.groupFriend.GroupFriendDto 멤버 친구 정보 DTO 클래스
     * @since 1.0
     * @author hyunjcho
     */
    @GetMapping(Define.WHERE42_VERSION_PATH + "/member/friend")
    public List<GroupFriendDto> groupFriendsInformation(@RequestParam Long id) {
        Member member = memberService.findById(id);
        return memberService.findAllFriendsInfo(member);
    }

    /**
     * 멤버 상태메시지 조회
     * @author hyunjcho
     * @since 1.0
     * @param req 세션 확인용 HttpServletRequest
     * @param res 토큰 확인용 HttpServletResponse
     * @param key 토큰 확인용 쿠키값
     * @return 멤버 상태메시지 String 반환
     * @throws TokenExpiredException 쿠키에 토큰key가 저장되어 있지않거나, db에 저장된 토큰이 없는 경우 // 이거 성훈이 확인 필
     * @throws UnregisteredMemberException 등록되지 않은 멤버의 경우 401 예외 throw
     * @see openproject.where42.token.TokenService#findAccessToken(HttpServletResponse, String) 토큰 쿠키 조회
     * @see openproject.where42.member.MemberService#findBySessionWithToken(HttpServletRequest, String) 세션 및 토큰으로 멤버 조회
     */
    @GetMapping(Define.WHERE42_VERSION_PATH + "/member/setting/msg")
    public String getPersonalMsg(HttpServletRequest req, HttpServletResponse res, @CookieValue(value = "ID", required = false) String key) {
        String token42 = tokenService.findAccessToken(res, key);
        Member member = memberService.findBySessionWithToken(req, token42);
        return member.getMsg();
    }

    /**
     * 멤버 상태메시지 설정
     * @param req 세션 확인용 HttpServletRequest
     * @param res 토큰 확인용 HttpServletResponse
     * @param key 토큰 확인용 쿠키값
     * @param msg 저장할 상태메시지
     * @return 저장 성공 반환
     * @throws TokenExpiredException 쿠키에 토큰key가 저장되어 있지않거나, db에 저장된 토큰이 없는 경우 // 이거 성훈이 확인 필
     * @throws UnregisteredMemberException 등록되지 않은 멤버의 경우 401 예외 throw
     * @see openproject.where42.token.TokenService#findAccessToken(HttpServletResponse, String) 토큰 쿠키 조회
     * @see openproject.where42.member.MemberService#updatePersonalMsg(HttpServletRequest, String, String) 상태메시지 업데이트
     * @since 1.0
     * @author hyunjcho
     */
    @PostMapping(Define.WHERE42_VERSION_PATH + "/member/setting/msg")
    public ResponseEntity updatePersonalMsg(HttpServletRequest req,  HttpServletResponse res, @CookieValue(value = "ID", required = false) String key, @RequestBody Map<String, String> msg) {
        String token42 = tokenService.findAccessToken(res, key);
        memberService.updatePersonalMsg(req, token42, msg.get("msg"));
        return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.SET_MSG), HttpStatus.OK);
    }

    /**
     * <pre>
     *     멤버 위치 설정 가능 여부 조회
     *     예외 발생 시 해당 멤버는 수동 자리 설정이 불가하며 퇴근 및 아이맥 자리 정보로 위치 정보가 갱신됨
     * </pre>
     * @param req 세션 확인용 HttpServletRequest
     * @param res 토큰 확인용 HttpServletResponse
     * @param key 토큰 확인용 쿠키값
     * @return 설정 가능 반환
     * @throws TokenExpiredException
     * @throws UnregisteredMemberException 등록되지 않은 멤버의 경우 401 예외 throw
     * @throws OutStateException 퇴근 상태일 시 409 예외 throw
     * @throws TakenSeatException 아이맥 자리 정보가 있을 시 409 예외 throw
     * @throws ServiceUnavailableException 24hane api 정상 정보 조회 불가능 시 503 예외 throw
     * @see openproject.where42.token.TokenService#findAccessToken(HttpServletResponse, String) 토큰 쿠키 조회
     * @see openproject.where42.member.MemberService#checkLocate(HttpServletRequest, String) 위치 설정 가능 여부 조회
     * @since 1.0
     * @author hyunjcho
     */
    @GetMapping(Define.WHERE42_VERSION_PATH + "/member/setting/locate")
    public ResponseEntity checkLocate(HttpServletRequest req, HttpServletResponse res, @CookieValue(value = "ID", required = false) String key)
            throws OutStateException, TakenSeatException, ServiceUnavailableException {
        String token42 = tokenService.findAccessToken(res, key);
        int planet = memberService.checkLocate(req, token42);
        return new ResponseEntity(ResponseWithData.res(StatusCode.OK, ResponseMsg.NOT_TAKEN_SEAT, planet), HttpStatus.OK);
    }

    /**
     * 멤버 수동 위치 설정 및 통계 처리를 위해 정보 저장
     * @param req 세션 확인용 HttpServletRequest
     * @param res 토큰 확인용 HttpServletResponse
     * @param key 토큰 확인용 쿠키값
     * @param locate 설정할 planet, floor, cluster, spot 정보가 담긴 클래스
     * @return 위치 설정 성공 반환
     * @throws TokenExpiredException 쿠키에 토큰key가 저장되어 있지않거나, db에 저장된 토큰이 없는 경우 // 이거 성훈이 확인 필
     * @throws UnregisteredMemberException 등록되지 않은 멤버의 경우 401 예외 throw
     * @see openproject.where42.token.TokenService#findAccessToken(HttpServletResponse, String) 토큰 쿠키 조회
     * @see openproject.where42.member.MemberService#findBySessionWithToken(HttpServletRequest, String) 세션 및 토큰으로 멤버 조회
     * @see openproject.where42.member.MemberService#updateLocate(Member, Locate) 멤버 위치 정보 갱신
     * @see MemberService#saveLocateDate(String, Locate) 수동 위치 정보 저장
     * @see openproject.where42.member.entity.Locate
     * @since 1.0
     * @author hyunjcho
     */
    @PostMapping(Define.WHERE42_VERSION_PATH + "/member/setting/locate")
    public ResponseEntity updateLocate(HttpServletRequest req, HttpServletResponse res, @CookieValue(value = "ID", required = false) String key, @RequestBody Locate locate) {
        String token42 = tokenService.findAccessToken(res, key);
        Member member = memberService.findBySessionWithToken(req, token42);
        memberService.updateLocate(member, locate);
        log.info("[setting] \"{}\"님이 \"p:{}, f:{}, c:{}, s:{}\" (으)로 위치를 수동 변경하였습니다.", member.getName(),
                locate.getPlanet(), locate.getFloor(), locate.getCluster(), locate.getSpot());
        memberService.saveLocateDate(member.getName(), locate);
        return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.SET_LOCATE), HttpStatus.OK);
    }

    /**
     * <pre>
     *     동료평가 정보 설정 가능 여부 조회 후 동료평가 상태 설정
     *     퇴근상태일 경우 설정 불가
     *     향후 42api와 연계하여 자동으로 동료평가 설정을 할 수 있도록 변경하는 것이 좋을 것 같음
     * </pre>*
     * @param req 세션 확인용 HttpServletRequest
     * @param res 토큰 확인용 HttpServletResponse
     * @param key 토큰 확인용 쿠키값
     * @return 동료평가 상태 설정 성공 반환
     * @throws TokenExpiredException 쿠키에 토큰key가 저장되어 있지않거나, db에 저장된 토큰이 없는 경우 // 이거 성훈이 확인 필
     * @throws UnregisteredMemberException 등록되지 않은 멤버의 경우 401 예외 throw
     * @throws OutStateException 퇴근 상태일 시 409 예외 throw
     * @throws ServiceUnavailableException 24hane api 정상 정보 조회 불가능 시 503 예외 throw
     * @see openproject.where42.token.TokenService#findAccessToken(HttpServletResponse, String) 토큰 쿠키 조회
     * @see openproject.where42.member.MemberService#findBySessionWithToken(HttpServletRequest, String) 세션 및 토큰으로 멤버 조회
     * @see MemberService#updateEvalOn(HttpServletRequest, String) 동료평가 상태 설정 가능 여부 조회 및 설정
     * @since 2.0
     * @author hyunjcho
     */
    @PostMapping(Define.WHERE42_VERSION_PATH + "/member/evalon")
    public ResponseEntity updateEvalOn(HttpServletRequest req, HttpServletResponse res, @CookieValue(value = "ID", required = false) String key)
        throws OutStateException, ServiceUnavailableException {
        String token42 = tokenService.findAccessToken(res, key);
        Member member = memberService.findBySessionWithToken(req, token42);
        memberService.updateEvalOn(req, token42);
        log.info("[setting] \"{}\"님이 동료 평가 중으로 상태를 변경하였습니다.", member.getName());
        return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.SET_EVAL_ON), HttpStatus.OK);
    }

    /**
     * 별도 확인 없이 동료평가 상태 설정 해제
     * @param req 세션 확인용 HttpServletRequest
     * @param res 토큰 확인용 HttpServletResponse
     * @param key 토큰 확인용 쿠키값
     * @return 동료평가 상태 설정 해제 성공 반환
     * @see openproject.where42.token.TokenService#findAccessToken(HttpServletResponse, String) 토큰 쿠키 조회
     * @see openproject.where42.member.MemberService#findBySessionWithToken(HttpServletRequest, String) 세션 및 토큰으로 멤버 조회
     * @see MemberService#updateEvalOff(HttpServletRequest, String) 동료평가 상태 설정 해제
     * @since 2.0
     * @author hyunjcho
     */
    @PostMapping(Define.WHERE42_VERSION_PATH + "/member/evaloff")
    public ResponseEntity updateEvalOff(HttpServletRequest req, HttpServletResponse res, @CookieValue(value = "ID", required = false) String key) {
        String token42 = tokenService.findAccessToken(res, key);
        Member member = memberService.findBySessionWithToken(req, token42);
        memberService.updateEvalOff(req, token42);
        log.info("[setting] \"{}\"님이 동료 평가 상태를 해제하였습니다.", member.getName());
        return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.SET_EVAL_OFF), HttpStatus.OK);
    }
}