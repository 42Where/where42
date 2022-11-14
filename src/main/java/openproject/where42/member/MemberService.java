package openproject.where42.member;

import lombok.RequiredArgsConstructor;
import openproject.where42.api.ApiService;
import openproject.where42.api.Define;
import openproject.where42.api.dto.Utils;
import openproject.where42.api.dto.Seoul42;
import openproject.where42.exception.OutStateException;
import openproject.where42.exception.SessionExpiredException;
import openproject.where42.exception.TakenSeatException;
import openproject.where42.group.GroupService;
import openproject.where42.group.domain.Groups;
import openproject.where42.group.GroupRepository;
import openproject.where42.groupFriend.GroupFriendRepository;
import openproject.where42.groupFriend.domain.GroupFriend;
import openproject.where42.groupFriend.GroupFriendDto;
import openproject.where42.member.domain.Locate;
import openproject.where42.member.domain.Member;
import openproject.where42.member.domain.enums.MemberLevel;
import openproject.where42.member.dto.MemberGroupInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final GroupService groupService;
    private final GroupRepository groupRepository;
    private final GroupFriendRepository groupFriendRepository;
    private final ApiService api;

    @Transactional
    public Long saveMember(String name, String img, String location) { // me 다시 부르지 않기 위해 img, location 정리하기
        Member member = new Member(name, img, MemberLevel.member); // member img 수정되면 이거 살리기
        String tokenHane = "하네 토큰";
        Long memberId = memberRepository.save(member);
        Long defaultGroupId = groupService.createDefaultGroup(member, "기본");
        Long starredGroupId = groupService.createDefaultGroup(member, "즐겨찾기");
        member.setDefaultGroup(defaultGroupId, starredGroupId);
        if (api.getHaneInfo(tokenHane, name) == Define.IN && location != null)
            updateLocate(member, Utils.parseLocate(location));
        else
            initLocate(member);
        return memberId;
    }

    public Member findBySession(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) {
//            session.invalidate(); // 이미 expired 된건데 하는 게 의미가 있나? 로그아웃시에는 해줘야함 잊지말자!
            throw new SessionExpiredException();
        }
        session.setMaxInactiveInterval(1 * 60); // 이걸 따로 설정 안해줘도 되는 거 같은데 일단 시간 지나는거보고 확인해야할듯
        return memberRepository.findById((Long)session.getAttribute("id"));
    }

    @Transactional
    public void updatePersonalMsg(HttpServletRequest req, String msg) {
        Member member = findBySession(req);

        member.updatePersonalMsg(msg);
    }

    public void checkLocate(HttpServletRequest req, String tokenHane, String token42) {
        Member member = findBySession(req);

        if (api.getHaneInfo(tokenHane, member.getName()) == Define.IN) {// hane 출근 확인 로직
            Seoul42 member42 = api.get42ShortInfo(token42, member.getName());
            if (member42.getLocation() != null) {
                updateLocate(member, Utils.parseLocate(member42.getLocation()));
                throw new TakenSeatException();
            }
        }
        else {
            initLocate(member);
            throw new OutStateException();
        }
    }
    @Transactional
    public void updateLocate(Member member, Locate locate) {
        member.getLocate().updateLocate(locate.getPlanet(), locate.getFloor(), locate.getCluster(), locate.getSpot());
    }

    @Transactional
    public void initLocate(Member member) {
        member.getLocate().updateLocate(null, 0, 0, null);
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

    public List<GroupFriendDto> findAllFriendsInfo(Member member, String token42, String tokenHane) {
        List<GroupFriendDto> friendsInfo = new ArrayList<GroupFriendDto>();
        List<GroupFriend> friends = groupFriendRepository.findAllGroupFriendByOwnerId(member.getDefaultGroupId());

        for (GroupFriend f : friends)
            friendsInfo.add(new GroupFriendDto(token42, tokenHane, f, memberRepository.findByName(f.getFriendName())));
        return friendsInfo;
    }
}