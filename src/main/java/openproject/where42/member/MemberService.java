package openproject.where42.member;

import lombok.RequiredArgsConstructor;
import openproject.where42.api.Define;
import openproject.where42.api.Utils;
import openproject.where42.group.GroupService;
import openproject.where42.group.domain.Groups;
import openproject.where42.group.GroupRepository;
import openproject.where42.groupFriend.GroupFriendService;
import openproject.where42.groupFriend.domain.GroupFriend;
import openproject.where42.groupFriend.dto.GroupFriendInfoDto;
import openproject.where42.member.domain.Locate;
import openproject.where42.member.domain.Member;
import openproject.where42.member.domain.enums.MemberLevel;
import openproject.where42.member.dto.MemberGroupInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final GroupService groupService;
    private final GroupFriendService groupFriendService;
    private final GroupRepository groupRepository;

    @Transactional
    public Long saveMember(String name, String img, String location) { // me 다시 부르지 않기 위해 img, location 정리하기
        Member member = new Member(name, img, MemberLevel.member); // member img 수정되면 이거 살리기
        Long memberId = memberRepository.save(member);
        Long defaultGroupId = groupService.createDefaultGroup(member, "기본");
        Long starredGroupId = groupService.createDefaultGroup(member, "즐겨찾기");

        member.setDefaultGroup(defaultGroupId, starredGroupId);
        if (3 == 3 && location != null) // hane 확인 로직
            updateLocate(member, Utils.parseLocate(location));
        else
            initializeLocate(member);
        return memberId;
    }

    public Member findByName(String name) {
        return memberRepository.findByName(name);
    }

    @Transactional
    public void updatePersonalMsg(Long memberId, String msg) {
        Member member = memberRepository.findById(memberId);

        member.updatePersonalMsg(msg);
    }

    @Transactional
    public void updateLocate(Member member, Locate locate) {
        member.getLocate().updateLocate(locate.getPlanet(), locate.getFloor(), locate.getCluster(), locate.getSpot());
    }

    @Transactional
    public void initializeLocate(Member member) {
        member.getLocate().updateLocate(null, 0, 0, null);
    }

    public List<MemberGroupInfo> findAllGroupFriendsInfo(Member member) {
        List<MemberGroupInfo> groupsInfo = new ArrayList<MemberGroupInfo>();
        List<Groups> customGroupList = groupService.findAllGroupsExceptDefault(member.getId());

        groupsInfo.add(new MemberGroupInfo(groupRepository.findById(member.getStarredGroupId()),
                groupFriendService.findAllGroupFriendNameByGroupId(member.getStarredGroupId())));
        for (Groups g : customGroupList)
            groupsInfo.add(new MemberGroupInfo(g, groupFriendService.findAllGroupFriendNameByGroupId(g.getId())));
        groupsInfo.add(new MemberGroupInfo(groupRepository.findById(member.getDefaultGroupId()),
                groupFriendService.findAllGroupFriendNameByGroupId(member.getDefaultGroupId())));
        return groupsInfo;
    }

    public List<GroupFriendInfoDto> findAllFriendsInfo(Member member) {
        List<GroupFriendInfoDto> friendsInfo = new ArrayList<GroupFriendInfoDto>();
        List<GroupFriend> friends = groupFriendService.findAllFriends(member.getDefaultGroupId());

        for (GroupFriend f : friends)
            friendsInfo.add(new GroupFriendInfoDto(f, member));
        return friendsInfo;
    }
}