package openproject.where42.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openproject.where42.api.ApiService;
import openproject.where42.exception.customException.*;
import openproject.where42.admin.AdminRepository;
import openproject.where42.util.Define;
import openproject.where42.api.mapper.Seoul42;
import openproject.where42.flashData.FlashDataService;
import openproject.where42.group.GroupService;
import openproject.where42.group.Groups;
import openproject.where42.group.GroupRepository;
import openproject.where42.groupFriend.GroupFriendRepository;
import openproject.where42.groupFriend.GroupFriend;
import openproject.where42.groupFriend.GroupFriendDto;
import openproject.where42.flashData.FlashData;
import openproject.where42.member.entity.Locate;
import openproject.where42.member.entity.Member;
import openproject.where42.member.entity.enums.MemberLevel;
import openproject.where42.member.dto.MemberGroupInfo;
import openproject.where42.member.entity.enums.Planet;
import openproject.where42.token.TokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * 멤버 관련 서비스 클래스
 * @version 2.0
 * @see openproject.where42.member
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class MemberService {
    private final MemberRepository memberRepository;
    private final GroupService groupService;
    private final GroupRepository groupRepository;
    private final GroupFriendRepository groupFriendRepository;
    private final FlashDataService flashDataService;
    private final ApiService apiService;
    private final TokenRepository tokenRepository;
    private final AdminRepository adminRepository;

    /**
     * <pre>
     *      인자로 받은 이름, 이미지 주소, 아이맥 정보, 피신 시작일을 바탕으로 멤버 생성
     *      피신 시작일의 경우 ver2 업데이트 시 추가되었으며, 기수 구분 통계용으로 활용 예정
     *      멤버 생성 후 해당 멤버의 기본 및 즐겨찾기 그룹을 생성하며 해당 그룹의 아이디를 멤버 정보로 저장하여
     *      이후 멤버의 기본 및 즐겨찾기 그룹 조회에 사용할 수 있도록 함
     *      인자로 받은 아이맥정보와 hane 정보를 토대로 현재 위치 정보를 갱신하여 저장함
     *      42api는 새로 갱신하지 않으며 hane api만 확인하여 위치 정보 파싱 진행
     * </pre>
     * @param name 멤버 이름
     * @param img 멤버 이미지 주소
     * @param location 멤버 42api 아이맥 정보
     * @param signUpDate ver2에 추가된 피신 시작일 정보
     * @return 생성 된 멤버 아이디 반환
     * @see openproject.where42.group.GroupService#createDefaultGroup(Member, String) 기본 그룹 생성
     * @see #parseStatus(Member, Planet) 위치 정보 파싱
     * @see ApiService#getHaneInfo(String, String) 하네 정보 조회
     * @since 1.0
     * @author hyunjcho
     */
    @Transactional
    public Long saveMember(String name, String img, String location, String signUpDate) {
        Member member = new Member(name, img, location, signUpDate, MemberLevel.member);
        Long memberId = memberRepository.save(member);
        Long defaultGroupId = groupService.createDefaultGroup(member, "기본");
        Long starredGroupId = groupService.createDefaultGroup(member, "즐겨찾기");
        member.setDefaultGroup(defaultGroupId, starredGroupId);
        parseStatus(member, apiService.getHaneInfo(name, adminRepository.callHane()));
        log.info("[member-create] \"{}\"님이 멤버로 등록되었습니다.", name);
        return memberId;
    }

    /**
     * <pre>
     *      세션 및 토큰을 통해 멤버 검색
     *      세션이 있는 경우 세션 시간 연장
     *      세션이 파기된 경우 토큰을 통해 멤버를 검색 후 세션 생성
     * </pre>
     * @param req 세션 확인용 HttpServletRequest
     * @param token42 쿠키를 통해 찾은 멤버 조회용 사용자 개별 access token
     * @return 세션 혹은 토큰을 통해 찾은 멤버 반환
     * @throws TokenExpiredException 토큰 만료 401 예외 throw
     * @throws UnregisteredMemberException 등록되지 않은 멤버의 경우 401 예외 throw
     * @see openproject.where42.member.MemberRepository#findIdByToken(String)
     * @see openproject.where42.member.MemberRepository#findById(Long)
     * @since 1.0
     * @author hyunjcho
     */
    public Member findBySessionWithToken(HttpServletRequest req, String token42) {
        HttpSession session = req.getSession(false);
        if (session == null) {
            Long memberId = memberRepository.findIdByToken(token42);
            if (memberId == 0)
                throw new UnregisteredMemberException();
            session = req.getSession();
            session.setAttribute("id", memberId);
        }
        session.setMaxInactiveInterval(60 * 60);
        return memberRepository.findById((Long)session.getAttribute("id"));
    }

    /**
     * 인자로 전달된 id를 통해 멤버 검색
     * @param id 멤버 id
     * @return id를 통해 찾은 멤버 반환
     * @see openproject.where42.member.MemberRepository#findById(Long)
     * @since 1.0
     * @author hyunjcho
     */
    public Member findById(Long id) {
        return memberRepository.findById(id);
    }

    /**
     * <pre>
     *     세션을 통해 멤버 검색
     *     토큰이 있는 상황에서만 사용 가능
     * </pre>
     * @param req 세션 확인용 HttpServletRequest
     * @return 세션을 통해 찾은 멤버 반환
     * @see openproject.where42.member.MemberRepository#findById(Long)
     * @since 1.0
     * @author hyunjcho
     */
    public Member findBySession(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        session.setMaxInactiveInterval(60 * 60);
        return memberRepository.findById((Long)session.getAttribute("id"));
    }

    /**
     * 인자로 받은 msg로 멤버 상태메시지 변경
     * @param req 세션 확인용 HttpServletRequest
     * @param token42 쿠키를 통해 찾은 멤버 조회용 사용자 개별 access token
     * @param msg 변경할 상태메시지
     * @throws TokenExpiredException 토큰 만료 401 예외 throw
     * @throws UnregisteredMemberException 멤버를 찾지 못했을 경우 401 예외 throw
     * @see #findBySessionWithToken(HttpServletRequest, String)
     * @since 1.0
     * @author hyunjcho
     */
    @Transactional
    public void updatePersonalMsg(HttpServletRequest req, String token42, String msg) {
        Member member = findBySessionWithToken(req, token42);
        log.info("[setting] \"{}\"님이 상태메세지를 [{}] (으)로 변경하였습니다.", member.getName(), msg);
        member.updatePersonalMsg(msg);
    }

    /**
     * <pre>
     *      인자로 받은 locate로 멤버의 위치 정보 갱신
     *      이 경우 locate는 사용자가 직접 설정한 정보임
     * </pre>
     * @param member locate를 변경 할 멤버
     * @param locate 변경할 locate
     * @since 1.0
     * @author hyunjcho
     */
    @Transactional
    public void updateLocate(Member member, Locate locate) {
        log.info("[setting] \"{}\"님의 Locate가 \"P: {}, F: {}, C: {}, S: {}\" 에서 \"P: {}, F: {}, C: {}, S: {}\" (으)로 바뀌었습니다.", member.getName()
                ,member.getLocate().getPlanet(), member.getLocate().getFloor(), member.getLocate().getCluster(), member.getLocate().getSpot()
                ,locate.getPlanet(),locate.getFloor(), locate.getCluster(), locate.getSpot());
        member.getLocate().updateLocate(locate.getPlanet(), locate.getFloor(), locate.getCluster(), locate.getSpot());
    }

    /**
     * <pre>
     *     수동 자리 정보 사용 통계를 위한 저장 함수
     *     저장 시점별, 자주 사용하는 장소 등 향후 통계 처리
     * </pre>
     * @param name 수동 자리 설정 멤버
     * @param locate 수동 자리 설정 위치
     * @since 2.0
     * @author hyunjcho
     */
    @Transactional void saveLocateDate(String name, Locate locate) {
        memberRepository.saveLocateData(name, locate);
    }

    /**
     * 인자로 받은 planet으로 멤버의 planet을 변경 후 나머지 위치 정보를 전부 초기화
     * @param member 위치 정보를 초기화 할 멤버
     * @param planet 갱신할 planet
     * @since 1.0
     * @author hyunjcho
     */
    @Transactional
    public void initLocate(Member member, Planet planet) {
        log.info("[setting] \"{}\"님의 Locate가  \"P: {}, F: 0, C: 0, S: null\"로 바뀌었습니다.", member.getName(), planet);
        member.getLocate().updateLocate(planet, 0, 0, null);
    }

    /**
     * <pre>
     *      백그라운드에서 멤버의 아이맥 좌석 정보 변경 감지 시 해당 함수를 호출하여 멤버의 planet 및 location 정보 갱신
     *      이때 planet은 hane api를 실시간으로 조회한 결과임
     *      location은 저장만 해둔 상태이며 해당 좌석 정보 파싱이 필요한 시점에 파싱함
     *      [updateTime, location(42api location 정보) 갱신]
     * </pre>
     * @param member planet 및 location 정보를 갱신할 멤버
     * @param planet 갱신할 planet
     * @param location 갱신할 location
     * @since 1.0
     * @author hyunjcho
     */
    @Transactional
    public void updateBackInfo(Member member, Planet planet, String location) {
        if (member.getLocate().getPlanet() != planet)
            member.updatePlanet(planet);
        member.updateLocation(location);
    }

    /**
     * <pre>
     *     멤버가 수동 자리 설정을 할 수 있는 지 여부 조회
     *     updateTime과 관계없이 24hane 및 42api를 호출하여 정보 확인 진행
     *     출근 상태이며 아이맥 정보가 없을 경우에만 정상 반환
     *     24hane를 통해 받아온 planet과 멤버의 planet이 다를 경우 및 멤버의 spot에 아이맥 정보가 남아있을 경우
     *     해당 planet 정보로 초기화 후 반환함
     *     (멤버 수동자리 설정 -> 아이맥 로그인 -> 아이맥 로그아웃동안 parsing이 진행되지 않았다면
     *     spot에는 수동자리, location에는 null이 들어있음.
     *     따라서 spot에 정보가 있는 경우 c로 시작할 경우 아이맥 정보, 아닐 경우 수동자리이며
     *     spot에 c가 있는 경우, c가 아니나 location이 parsed가 아닌 경우 초기화를 진행함)
     *     ver2 업데이트 시 외출 상태 추가
     *     24hane Api 오류 발생 시 planet error로 초기화하며 hane 조회 불가로 인해 수동 자리 설정 불가 안내
     *     예외 발생 시 checked excpetion으로 위치 정보는 그대로 유지됨
     *     [updateTime, inOrOut, 출근 상태일 경우 - location(parsed) 갱신 + locate parsing 진행, 외출/퇴근 상태일 경우 - locate parsing 진행하지 않음]
     * </pre>
     * @param req 세션 확인용 HttpServletRequest
     * @param token42 쿠키를 통해 찾은 멤버 조회용 사용자 개별 access token
     * @return 멤버가 현재 입실해 있는 planet 정보 반환
     * @throws TokenExpiredException
     * @throws UnregisteredMemberException 등록되지 않은 멤버의 경우 401 예외 throw
     * @throws OutStateException hane 조회 결과 퇴근 상태일 경우 퇴근 상태로 초기화 후 409 예외 throw
     * @throws TakenSeatException 42 조회 결과 아이맥 자리 정보가 있을 경우 해당 아이맥 정보로 위치 정보 갱신 후 409 예외 throw
     * @throws ServiceUnavailableException 24hane api 정상 조회 불가능 시 503 예외 throw
     * @see #initLocate(Member, Planet) 퇴근, planet 정보가 다른 경우 및 spot에 아이맥 정보가 남아있는 경우 초기화
     * @see #updateLocate(Member, Locate) 42 api 조회 후 아이맥 정보가 있을 경우 해당 아이맥 정보를 파싱하여 위치 정보 갱신
     * @since 1.0
     * @author hyunjcho
     */
    @Transactional
    public int checkLocate(HttpServletRequest req, String token42) throws OutStateException, TakenSeatException, ServiceUnavailableException {
        Member member = findBySessionWithToken(req, token42);
        Planet planet = apiService.getHaneInfo(member.getName(), adminRepository.callHane());
        if (planet == null) {
            log.info("[member-check-locate] \"{}\"님이 퇴근 상태로 수동 자리 설정을 할 수 없습니다.", member.getName());
            member.updateOutStatus(Define.OUT);
            throw new OutStateException();
        }
        if (planet == Planet.rest) {
            log.info("[member-check-locate] \"{}\"님이 외출 상태로 수동 자리 설정을 할 수 없습니다.", member.getName());
            member.updateOutStatus(Define.REST);
            throw new OutStateException();
        }
        Seoul42 member42 = apiService.getMeInfo(token42);
        if (member42.getLocation() != null) {
            log.info("[member-check-locate] \"{}\"님의 아이맥 자리 정보가 있기 떄문에 수동 자리 설정을 할 수 없습니다.", member.getName());
            updateLocate(member, Locate.parseLocate(member42.getLocation()));
            member.updateParsedStatus(Define.IN);
            throw new TakenSeatException();
        }
        if (member.getLocate().getPlanet() != planet || (member.getLocate().getSpot() != null
                && (member.getLocate().getSpot().charAt(0) == 'c') || !Define.PARSED.equalsIgnoreCase(member.getLocation())))
            initLocate(member, planet);
        if (planet == Planet.error) {
            log.info("[member-check-locate] \"{}\"님의 하네 정보에 오류가 있기 때문에 수동 자리 설정을 할 수 없습니다.", member.getName());
            member.updateParsedStatus(Define.ERROR);
            throw new ServiceUnavailableException();
        }
        member.updateParsedStatus(Define.IN);
        return planet.getValue();
    }

    /**
     * <pre>
     *     멤버가 동료평가 설정이 가능한지 여부 조회 후 가능할 경우 상태 설정 및 설정 시각 갱신
     *     멤버의 정보 갱신 시각이 1분 이상일 경우 api를 호출하며, 1분 미만일 경우 현재 가지고 있는 정보로 확인
     *     퇴근 및 외출 상태일 경우 설정이 불가하며, hane 오류 시에도 불가함
     * </pre>
     * @param req 세션 확인용 HttpServletRequest
     * @param token42 쿠키를 통해 찾은 멤버 조회용 사용자 개별 access token
     * @throws OutStateException 퇴근, 외출 상태일 시 409 예외 throw
     * @throws ServiceUnavailableException 24hane api 정상 조회 불가능 시 503 예외 throw
     * @since 2.0
     * @author hyunjcho
     */
    @Transactional
    public void updateEvalOn(HttpServletRequest req, String token42) throws OutStateException, ServiceUnavailableException {
        Member member = findBySessionWithToken(req, token42);
        Planet planet;
        boolean updateFlag = member.timeDiff() > 0;
        if (updateFlag)
            planet = apiService.getHaneInfo(member.getName(), adminRepository.callHane());
        else
            planet = member.getLocate().getPlanet();
        if (planet == null) {
            log.info("[member-check-locate] \"{}\"님이 퇴근 상태로 동료평가 설정을 할 수 없습니다.", member.getName());
            member.updateOutStatus(Define.OUT);
            throw new OutStateException();
        }
        if (planet == Planet.rest) {
            log.info("[member-check-locate] \"{}\"님이 외출 상태로 동료평가 설정을 할 수 없습니다.", member.getName());
            member.updateOutStatus(Define.REST);
            throw new OutStateException();
        }
        if (planet == Planet.error) {
            member.updateOutStatus(Define.ERROR);
            log.info("[member-check-locate] \"{}\"님의 하네 정보에 오류가 있기 때문에 동료평가 설정을 할 수 없습니다.", member.getName());
            throw new ServiceUnavailableException();
        }
        if (updateFlag) {
            Seoul42 member42 = apiService.getMeInfo(token42);
            if (member42.getLocation() != null)
                updateLocate(member, Locate.parseLocate(member42.getLocation()));
            else if (member.getLocate().getPlanet() != planet || (member.getLocate().getSpot() != null
                    && (member.getLocate().getSpot().charAt(0) == 'c') || !Define.PARSED.equalsIgnoreCase(member.getLocation())))
                initLocate(member, planet);
            member.updateParsedStatus(Define.IN);
        }
        member.updateEval(Define.EVALON);
    }

    /**
     * <pre>
     *     동료평가 설정 상태 해제
     *     현재 동료평가 설정 여부 및 설정 시각 확인하지 않고 항상 off 상태로 변경
     * </pre>
     * @param req 세션 확인용 HttpServletRequest
     * @param token42 쿠키를 통해 찾은 멤버 조회용 사용자 개별 access token
     * @throws TokenExpiredException
     * @throws UnregisteredMemberException 등록되지 않은 멤버의 경우 401 예외 throw
     * @since 2.0
     * @author hyunjcho
     */
    @Transactional
    public void updateEvalOff(HttpServletRequest req, String token42) {
        Member member = findBySessionWithToken(req, token42);
        member.updateEval(Define.EVALOFF);
    }

    /**
     * <pre>
     *      멤버 정보 갱신 시간이 1분이 지난 상태에서 멤버가 본인의 정보를 조회할 경우 호출
     *      42 및 hane api를 호출하여 최신 정보로 갱신
     *      hane 정보를 우선으로 하여 출근일 경우에만 42api를 조회하며 퇴근일 경우 42api 조회하지 않음
     *      이는 인트라 오류를 방지하기 위한 것으로 임시 출입증 사용시 해당 사용자의 입실 여부를 확인할 수 없으므로 오류가 발생할 수 있음
     *      임시출입증 사용시에도 아이맥 정보를 반영해주고 싶을 경우 사용자에게 해당 정보가 맞는지 등을 확인하는 로직을 추가하여 개발하여야 함
     *      (다만 24hane에서 제공하는 planet 정보와 42api에서 제공하는 로케이션의 플래닛 정보 일치 여부는 확인하지 않음,
     *      안정성을 높이고 싶을 경우 해당 정보에 따른 조건 검사가 추가로 필요함)
     *      ver2 업데이트 시 외출 및 동료평가 상태 정보 추가함
     *      동료평가 상태가 설정되어있고 설정 시각으로부터 30분이 지났거나, 외출 또는 퇴근인 경우 동료평가 상태를 설정 해제함
     *      24hane 오류시 hane 정보는 확인하지 않고 42api 정보만으로 정보 파싱하며 해당 정보가 오류가 있음을 inOrOut에 표기
     *      [updateTime, inOrOut, location(parsed) 갱신 + locate parsing 진행]
     * </pre>
     * @param member 자리 정보를 파싱할 멤버
     * @param token42 42api 조회용 사용자 개별 access token
     * @since 1.0
     * @author hyunjcho
     */
    @Transactional
    public void parseStatus(Member member, String token42) {
        Planet planet = apiService.getHaneInfo(member.getName(), adminRepository.callHane());
        if (planet == Planet.gaepo || planet == Planet.seocho || planet == Planet.error) {
            Seoul42 seoul42 = apiService.getMeInfo(token42);
            if (seoul42.getLocation() != null)
                updateLocate(member, Locate.parseLocate(seoul42.getLocation()));
            else {
                if (member.getLocate().getPlanet() != planet || (member.getLocate().getSpot() != null
                    && (member.getLocate().getSpot().charAt(0) == 'c' || !Define.PARSED.equalsIgnoreCase(member.getLocation()))))
                    initLocate(member, planet);
            }
            if (planet != Planet.error)
                member.updateParsedStatus(Define.IN);
            else
                member.updateParsedStatus(Define.ERROR);
        } else if (planet == Planet.rest)
            member.updateOutStatus(Define.REST);
        else
            member.updateOutStatus(Define.OUT);
        if (member.getEvaling() == Define.EVALON && (member.evalTimeDiff() > 29 || member.getInOrOut() != Define.IN))
            member.updateEval(Define.EVALOFF);
    }

    /**
     * <pre>
     *      멤버 정보 갱신 시간이 1분이 지나지 않은 상태에서 본인 정보를 조회하거나, 친구가 멤버인 경우 정보 갱신 시간이 3분이 지나지 않은 상태에서
     *      해당 멤버의 location 정보가 parsed가 아닐 경우 진행
     *      24hane api는 경우에 따라 호출하나, 42api는 호출하지 않고 백그라운드 업데이트 정보를 그대로 활용함
     *      (다만 24hane에서 제공하는 planet 정보와 42api에서 제공하는 로케이션의 플래닛 정보 일치 여부는 확인하지 않음,
     *      안정성을 높이고 싶을 경우 해당 정보에 따른 조건 검사가 추가로 필요함)
     *      ver2 업데이트 시 외출 및 동료평가 상태 정보 추가함
     *      퇴근/외출일 경우에는 아이맥 화면잠금 시각을 고려하여 자리정보를 초가화하지 않고 갖고 있다가
     *      다시 돌아왔을 경우 아이맥 로그인이 유지되어 있다면 해당 자리를 보여줄 수 있도록 함
     *      동료평가 상태가 설정되어있고 설정 시각으로부터 30분이 지났거나, 외출 또는 퇴근인 경우 동료평가 상태를 설정 해제함
     *      24hane 오류시 hane 정보는 확인하지 않고 42api 정보만으로 정보 파싱하며 해당 정보가 오류가 있음을 inOrOut에 표기
     *      [inOrOut, location(parsed) 갱신 + locate parsing 진행, updateTime 미갱신]
     * </pre>
     * @param member 자리 정보를 파싱할 멤버
     * @param planet 확인할 planet
     * @since 1.0
     * @author hyunjcho
     */
    @Transactional
    public void parseStatus(Member member, Planet planet) {
        if (planet == Planet.gaepo || planet == Planet.seocho || planet == Planet.error) {
            if (member.getLocation() != null && !Define.PARSED.equalsIgnoreCase(member.getLocation()))
                updateLocate(member, Locate.parseLocate(member.getLocation()));
            else if (member.getLocate().getPlanet() != planet || member.getLocation() == null && member.getLocate().getSpot() != null)
                initLocate(member, planet);
            if (planet != Planet.error)
                member.updateParsedInOrOut(Define.IN);
            else
                member.updateParsedInOrOut(Define.ERROR);
        } else if (planet == Planet.rest && member.getInOrOut() != Define.REST)
                member.updateInOrOut(Define.REST);
        else if (planet == null && member.getInOrOut() != Define.OUT)
                member.updateInOrOut(Define.OUT);
        if (member.getEvaling() == Define.EVALON && (member.evalTimeDiff() > 29 || member.getInOrOut() == Define.REST || member.getInOrOut() == Define.OUT))
            member.updateEval(Define.EVALOFF);
    }

    /**
     * <pre>
     *     멤버의 그룹별 친구 이름 및 그룹 관련 정보 조회
     *     즐겨찾기 -> 커스텀 그룹 알파벳 정렬순 -> 기본 그룹 순으로 정렬하며, 각 그룹 내 친구들은 이름 알파벳 순으로 정렬되어있음
     * </pre>
     * @param member 친구 정보를 조회할 멤버
     * @return 친구 정보 DTO 리스트
     * @see MemberGroupInfo 멤버의 그룹별 정보 DTO 클래스
     * @see GroupService#findAllGroupsExceptDefault(Long) 기본 및 즐겨찾기 그룹 제외 멤버 커스텀 그룹 전체 조회
     * @see GroupFriendRepository#findGroupFriendsByGroupId(Long) 그룹별 친구 이름 리스트 조회
     * @since 1.0
     * @author hyunjcho
     */
    public List<MemberGroupInfo> findAllGroupFriendsInfo(Member member) {
        List<MemberGroupInfo> groupsInfo = new ArrayList<MemberGroupInfo>();
        List<Groups> customGroupList = groupService.findAllGroupsExceptDefault(member.getId());
        groupsInfo.add(new MemberGroupInfo(groupRepository.findById(member.getStarredGroupId()),
                groupFriendRepository.findGroupFriendsByGroupId(member.getStarredGroupId())));
        for (Groups g : customGroupList)
            groupsInfo.add(new MemberGroupInfo(g, groupFriendRepository.findGroupFriendsByGroupId(g.getId())));
        groupsInfo.add(new MemberGroupInfo(groupRepository.findById(member.getDefaultGroupId()),
                groupFriendRepository.findGroupFriendsByGroupId(member.getDefaultGroupId())));
        return groupsInfo;
    }

    /**
     * <pre>
     *      멤버의 기본 그룹을 바탕으로 한 중복없는 모든 친구 정보 조회
     *      친구는 이름순으로 정렬되며 친구의 현재 상태를 조회함
     *      친구가 멤버일 경우와 아닐 경우를 나누어 조회함
     *      location 정보가 파싱되어있지 않을 경우 파싱 진행
     * </pre>
     * @param member 친구를 조회할 멤버
     * @return 전체 친구 정보 DTO 리스트
     * @see GroupFriendDto 친구 정보 DTO 클래스
     * @see GroupFriendRepository#findAllGroupFriendByOwnerId(Long) 해당 그룹의 친구 전체 반환
     * @see #checkMemberStatus(Member) 멤버인 친구의 상태 조회
     * @see FlashDataService#checkFlashFriend(Long, String) 플래시 데이터에 있는지 조회
     * @since 1.0
     * @author hyunjcho
     */
    @Transactional
    public List<GroupFriendDto> findAllFriendsInfo(Member member) {
        List<GroupFriendDto> friendsInfo = new ArrayList<GroupFriendDto>();
        List<GroupFriend> friends = groupFriendRepository.findAllGroupFriendByOwnerId(member.getDefaultGroupId());
        for (GroupFriend f : friends) {
            Member friend = memberRepository.findByName(f.getFriendName());
            if (friend != null) {
                checkMemberStatus(friend);
                friendsInfo.add(new GroupFriendDto(friend, f.getId()));
            } else {
                FlashData flash = flashDataService.checkFlashFriend(member.getDefaultGroupId(), f.getFriendName());
                friendsInfo.add(new GroupFriendDto(flash, f.getId(), f.getImg()));
            }
        }
        return friendsInfo;
    }

    /**
     * <pre>
     *     멤버 본인이 아닌 친구로서 정보 조회 시 3분을 기준으로 하여
     *     갱신 시각이 3분이 지났을 경우 24hane api를 새로 호출하여 파싱 진행
     *     지나지 않았을 경우 파싱되지 않은 경우 파싱 진행, 파싱되어있는 경우 eval 정보만 갱신
     *     [일부 updateTime 갱신]
     * </pre>
     * @param member 조회하고자 하는 멤버
     * @since 1.0
     * @author hyunjcho
     */
    @Transactional
    public void checkMemberStatus(Member member) {
        if (member.timeDiff() > 2) {
            parseStatus(member, apiService.getHaneInfo(member.getName(), adminRepository.callHane()));
            member.changeTime();
        } else {
            if (!Define.PARSED.equalsIgnoreCase(member.getLocation()))
                parseStatus(member, member.getLocate().getPlanet());
            else
                if (member.getEvaling() == Define.EVALON && (member.evalTimeDiff() > 29 || member.getInOrOut() == Define.REST || member.getInOrOut() == Define.OUT || member.getInOrOut() == Define.ERROR))
                    member.updateEval(Define.EVALOFF);
        }
    }
}