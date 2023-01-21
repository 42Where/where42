package openproject.where42.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openproject.where42.api.ApiService;
import openproject.where42.exception.customException.*;
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
import java.util.concurrent.CompletableFuture;

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

    @Transactional
    public Long saveMember(String name, String img, String location) {
        Member member = new Member(name, img, location, MemberLevel.member);
        Long memberId = memberRepository.save(member);
        Long defaultGroupId = groupService.createDefaultGroup(member, "기본");
        Long starredGroupId = groupService.createDefaultGroup(member, "즐겨찾기");
        member.setDefaultGroup(defaultGroupId, starredGroupId);
        parseStatus(member, apiService.getHaneInfo(name, tokenRepository.callHane()));
        log.info("[member-create] \"{}\"님이 멤버로 등록되었습니다.", name);
        return memberId;
    }

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

    public Member findById(Long id) {
        return memberRepository.findById(id);
    }

    public Member findBySession(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        session.setMaxInactiveInterval(60 * 60);
        return memberRepository.findById((Long)session.getAttribute("id"));
    }

    public boolean findAdminBySession(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null)
            throw new SessionExpiredException();
        session.setMaxInactiveInterval(60 * 60);
        return memberRepository.findByAdminName((String)session.getAttribute("name"));
    }

    @Transactional
    public void updatePersonalMsg(HttpServletRequest req, String token42, String msg) {
        Member member = findBySessionWithToken(req, token42);
        log.info("[setting] \"{}\"님이 상태메세지를 [{}] (으)로 변경하였습니다.", member.getName(), msg);
        member.updatePersonalMsg(msg);
    }

    @Transactional
    public void updateLocate(Member member, Locate locate) {
        log.info("[setting] \"{}\"님의 Locate가 \"P: {}, F: {}, C: {}, S: {}\" 에서 \"P: {}, F: {}, C: {}, S: {}\" (으)로 바뀌었습니다.", member.getName()
                ,member.getLocate().getPlanet(), member.getLocate().getFloor(), member.getLocate().getCluster(), member.getLocate().getSpot()
                ,locate.getPlanet(),locate.getFloor(), locate.getCluster(), locate.getSpot());
        member.getLocate().updateLocate(locate.getPlanet(), locate.getFloor(), locate.getCluster(), locate.getSpot());
    }

    @Transactional
    public void initLocate(Member member, Planet planet) {
        log.info("[setting] \"{}\"님의 Locate가  \"P: {}, F: 0, C: 0, S: null\"로 바뀌었습니다.", member.getName(), planet);
        member.getLocate().updateLocate(planet, 0, 0, null);
    }

    @Transactional
    public void updateBackInfo(Member member, Planet planet, String location) {
        if (member.getLocate().getPlanet() != planet)
            member.updatePlanet(planet);
        member.updateLocation(location);
    }

    // api 호출, [inOrOut 갱신, location(parsed), updateTime 갱신]
    @Transactional
    public int checkLocate(HttpServletRequest req, String token42) throws OutStateException, TakenSeatException {
        Member member = findBySessionWithToken(req, token42);
        Planet planet = apiService.getHaneInfo(member.getName(), tokenRepository.callHane());
        if (planet == null) {
            log.info("[member-check-locate] \"{}\"님이 퇴근 상태로 수동 자리 설정을 할 수 없습니다.", member.getName());
            initLocate(member, null);
            member.updateStatus(Define.OUT);
            throw new OutStateException();
        }
        CompletableFuture<Seoul42> cf = apiService.getMeInfo(token42);
        Seoul42 member42 = apiService.injectInfo(cf);
        if (member42.getLocation() != null) {
            log.info("[member-check-locate] \"{}\"님의 아이맥 자리 정보가 있기 떄문에 수동 자리 설정을 할 수 없습니다.", member.getName());
            updateLocate(member, Locate.parseLocate(member42.getLocation()));
            member.updateStatus(Define.IN);
            throw new TakenSeatException();
        }
        if (member.getLocate().getPlanet() != planet || (member.getLocate().getSpot() != null && member.getLocate().getSpot().charAt(0) == 'c'))
            initLocate(member, planet);
        member.updateStatus(Define.IN);
        return planet.getValue();
    }

    // 멤버 인포 조회용 api 호출, [inOrOut, location(parsed) 갱신], updateTime 갱신
    @Transactional
    public void parseStatus(Member member, String token42) {
        Planet planet = apiService.getHaneInfo(member.getName(), tokenRepository.callHane());
        if (planet != null) {
            CompletableFuture<Seoul42> cf = apiService.getMeInfo(token42);
            Seoul42 seoul42 = apiService.injectInfo(cf);
            if (seoul42.getLocation() != null)
                updateLocate(member, Locate.parseLocate(seoul42.getLocation()));
            else {
                if (member.getLocate().getPlanet() != planet || (member.getLocate().getSpot() != null && member.getLocate().getSpot().charAt(0) == 'c'))
                    initLocate(member, planet);
            }
            member.updateStatus(Define.IN);
        } else {
            initLocate(member, null);
            member.updateStatus(Define.OUT);
        }
    }

    // api 미호출, [inOrOut, location(parsed) 갱신], updateTime 미갱신
    @Transactional
    public void parseStatus(Member member, Planet planet) {
        if (planet != null) {
            if (member.getLocation() != null && !Define.PARSED.equalsIgnoreCase(member.getLocation()))
                updateLocate(member, Locate.parseLocate(member.getLocation()));
            else if (member.getLocate().getPlanet() != planet)
                initLocate(member, planet);
            else if (member.getLocation() == null && member.getLocate().getSpot() != null) {
                if (member.getLocate().getSpot().charAt(0) == 'c')
                    initLocate(member, planet);
                else
                    member.updateLocation(Define.PARSED);
            }
            member.updateInOrOut(Define.IN);
        } else {
            if (member.getInOrOut() == Define.IN) {
                initLocate(member, null);
                member.updateInOrOut(Define.OUT);
            }
        }
    }

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

    @Transactional
    public List<GroupFriendDto> findAllFriendsInfo(Member member) {
        List<GroupFriendDto> friendsInfo = new ArrayList<GroupFriendDto>();
        List<GroupFriend> friends = groupFriendRepository.findAllGroupFriendByOwnerId(member.getDefaultGroupId());
        for (GroupFriend f : friends) {
            Member friend = memberRepository.findByName(f.getFriendName());
            if (friend != null) {
                Planet planet = checkMemberStatus(friend);
                if (!Define.PARSED.equalsIgnoreCase(friend.getLocation()))
                    parseStatus(friend, planet);
                friendsInfo.add(new GroupFriendDto(friend, f.getId()));
            } else {
                FlashData flash = flashDataService.checkFlashFriend(member.getDefaultGroupId(), f.getFriendName());
                friendsInfo.add(new GroupFriendDto(flash, f.getId(), f.getImg()));
            }
        }
        return friendsInfo;
    }

    @Transactional
    public Planet checkMemberStatus(Member member) {
        if (member.timeDiff() > 3) {
            Planet planet = apiService.getHaneInfo(member.getName(), tokenRepository.callHane());
            if (member.getLocate().getPlanet() != planet) {
                initLocate(member, planet);
                if (planet != null)
                    member.updateStatus(Define.IN);
                else
                    member.updateStatus(Define.OUT);
            } else
                member.changeTime();
            return planet;
        }
        return member.getLocate().getPlanet();
    }
}