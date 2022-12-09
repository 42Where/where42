package openproject.where42.member;

import lombok.RequiredArgsConstructor;
import openproject.where42.api.ApiService;
import openproject.where42.api.Define;
import openproject.where42.api.dto.Seoul42;
import openproject.where42.exception.customException.OutStateException;
import openproject.where42.exception.customException.SessionExpiredException;
import openproject.where42.exception.customException.TakenSeatException;
import openproject.where42.group.GroupService;
import openproject.where42.group.entity.Groups;
import openproject.where42.group.GroupRepository;
import openproject.where42.groupFriend.GroupFriendRepository;
import openproject.where42.groupFriend.entity.GroupFriend;
import openproject.where42.groupFriend.entity.GroupFriendDto;
import openproject.where42.member.entity.FlashData;
import openproject.where42.member.entity.Locate;
import openproject.where42.member.entity.Member;
import openproject.where42.member.entity.enums.MemberLevel;
import openproject.where42.member.dto.MemberGroupInfo;
import openproject.where42.member.entity.enums.Planet;
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
public class MemberService {
    private final MemberRepository memberRepository;
    private final GroupService groupService;
    private final GroupRepository groupRepository;
    private final GroupFriendRepository groupFriendRepository;
    private final FlashDataService flashDataService;
    private final ApiService apiService;

    @Transactional
    public Long saveMember(String name, String img, String location) {
        Member member = new Member(name, img, location, MemberLevel.member);
        Long memberId = memberRepository.save(member);
        Long defaultGroupId = groupService.createDefaultGroup(member, "기본");
        Long starredGroupId = groupService.createDefaultGroup(member, "즐겨찾기");
        member.setDefaultGroup(defaultGroupId, starredGroupId);
        return memberId;
    }

    public Member findBySession(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null)
            throw new SessionExpiredException();
        session.setMaxInactiveInterval(30 * 30); // 이걸 따로 설정 안해줘도 되는 거 같은데 일단 시간 지나는거보고 확인해야할듯
        return memberRepository.findById((Long)session.getAttribute("id"));
    }

    @Transactional
    public void updatePersonalMsg(HttpServletRequest req, String msg) {
        Member member = findBySession(req);
        member.updatePersonalMsg(msg);
    }

    @Transactional
    public void updateLocate(Member member, Locate locate) {
        member.getLocate().updateLocate(locate.getPlanet(), locate.getFloor(), locate.getCluster(), locate.getSpot());
    }

    @Transactional
    public void initLocate(Member member, Planet planet) {
        member.getLocate().updateLocate(planet, 0, 0, null);
    }

    @Transactional
    public void updateLocation(Member member, String location) {
        member.updateLocation(location);
    }

    // api 호출, [inOrOut 갱신, location(parsed), updateTime 갱신]
    @Transactional
    public int checkLocate(HttpServletRequest req, String token42) throws OutStateException, TakenSeatException {
        Member member = findBySession(req);
        Planet planet = apiService.getHaneInfo(member.getName());
        if (planet == null) {
            initLocate(member, null);
            member.updateStatus(Define.OUT);
            throw new OutStateException();
        }
        CompletableFuture<Seoul42> cf = apiService.get42ShortInfo(token42, member.getName());
        Seoul42 member42 = apiService.injectInfo(cf);
        if (member42.getLocation() != null) {
            updateLocate(member, Locate.parseLocate(member42.getLocation()));
            throw new TakenSeatException();
        }
        member.updateStatus(Define.IN);
        return planet.getValue();
    }

    // 멤버 인포 조회용 api 호출, [inOrOut, location(parsed) 갱신], updateTime 미갱신
    @Transactional
    public void parseStatus(Member member, String token42) {
        Planet planet = apiService.getHaneInfo(member.getName());
        if (planet != null) {
            CompletableFuture<Seoul42> cf = apiService.get42ShortInfo(token42, member.getName());
            Seoul42 seoul42 = apiService.injectInfo(cf);
            if (seoul42.getLocation() != null)
                updateLocate(member, Locate.parseLocate(seoul42.getLocation()));
            else {
                if (member.getLocate().getPlanet() == null)
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
    public void parseStatus(Member member) {
        Planet planet = apiService.getHaneInfo(member.getName());
        if (planet != null) {
            if (member.getLocation() != null)
                updateLocate(member, Locate.parseLocate(member.getLocation()));
            else {
                if (member.getLocate().getPlanet() == null)
                    initLocate(member, planet);
            }
            member.updateInOrOut(Define.IN);
        } else {
            initLocate(member, planet);
            member.updateInOrOut(Define.OUT);
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
    public List<GroupFriendDto> findAllFriendsInfo(Member member, String token42) {
        List<GroupFriendDto> friendsInfo = new ArrayList<GroupFriendDto>();
        List<GroupFriend> friends = groupFriendRepository.findAllGroupFriendByOwnerId(member.getDefaultGroupId());
        for (GroupFriend f : friends) {
            Member friend = memberRepository.findMember(f.getFriendName());
            if (friend != null) {
                if (!Define.PARSED.equalsIgnoreCase(friend.getLocation()))
                        parseStatus(friend);
                friendsInfo.add(new GroupFriendDto(friend, f.getId()));
            } else {
                FlashData flash = flashDataService.checkFlashFriend(f.getFriendName(), token42);
                friendsInfo.add(new GroupFriendDto(flash, f.getId()));
            }
        }
        return friendsInfo;
    }

    @Transactional
    public void deleteMember(Long memberId) {
        memberRepository.deleteMember(memberId);
    }
}