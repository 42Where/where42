package openproject.where42.member;

import lombok.RequiredArgsConstructor;
import openproject.where42.api.Define;
import openproject.where42.api.Utils;
import openproject.where42.group.GroupService;
import openproject.where42.group.domain.Groups;
import openproject.where42.group.GroupRepository;
import openproject.where42.groupFriend.GroupFriendService;
import openproject.where42.groupFriend.domain.GroupFriend;
import openproject.where42.groupFriend.dto.GroupFriendInfo;
import openproject.where42.member.domain.Locate;
import openproject.where42.member.domain.Member;
import openproject.where42.member.domain.enums.MemberLevel;
import openproject.where42.member.dto.MemberGroupInfo;
import openproject.where42.member.dto.MemberInfo;
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
    public MemberInfo saveMember(String name, String img, String location) { // me 다시 부르지 않기 위해 img, location 정리하기
        Member member = new Member(name, MemberLevel.member);
        // Member member = new Member(name, img); // member img 수정되면 이거 살리기
        Long memberId = memberRepository.save(member);
        Long defaultGroupId = groupService.createDefaultGroup(member, "기본");
        Long starredGroupId = groupService.createDefaultGroup(member, "즐겨찾기");

        member.setDefaultGroup(defaultGroupId, starredGroupId);
        if (3 == 3 && location != null) {// hane 확인 로직
            updateLocate(memberId, Utils.parseLocate(location));
            return new MemberInfo(member, Define.IN);
        }
        else {
            initializeLocate(member);
            return new MemberInfo(member, Define.OUT);
        }
    }

    @Transactional
    public void updatePersonalMsg(Long memberId, String msg) {
        Member member = memberRepository.findById(memberId);

        member.updatePersonalMsg(msg);
    }

    @Transactional
    public void updateLocate(Long memberId, Locate locate) {
        Member member = memberRepository.findById(memberId);

        member.getLocate().updateLocate(locate.getPlanet(), locate.getFloor(), locate.getCluster(), locate.getSpot());
    }

    @Transactional
    public void initializeLocate(Member member) {
        member.getLocate().updateLocate(null, 0, 0, null);
    }

    public List<MemberGroupInfo> findAllGroupFriendsInfo(Long memberId) {
        List<MemberGroupInfo> groupsInfo = new ArrayList<MemberGroupInfo>();
        List<Groups> customGroupList = groupService.findAllGroupsExceptDefault(memberId);
        Member member = memberRepository.findById(memberId);

        groupsInfo.add(new MemberGroupInfo(groupRepository.findById(member.getStarredGroupId()),
                groupFriendService.findAllGroupFriendNameByGroupId(member.getStarredGroupId())));
        for (Groups g : customGroupList)
            groupsInfo.add(new MemberGroupInfo(g, groupFriendService.findAllGroupFriendNameByGroupId(g.getId())));
        groupsInfo.add(new MemberGroupInfo(groupRepository.findById(member.getDefaultGroupId()),
                groupFriendService.findAllGroupFriendNameByGroupId(member.getDefaultGroupId())));
        return groupsInfo;
    }

    public List<GroupFriendInfo> findAllFriendsInfo(Long memberId) {
        List<GroupFriendInfo> friendsInfo = new ArrayList<GroupFriendInfo>();
        Member member = memberRepository.findById(memberId);
        List<GroupFriend> friends = groupFriendService.findAllFriends(member.getDefaultGroupId());

        for (GroupFriend f : friends)
            friendsInfo.add(new GroupFriendInfo(f));
        return friendsInfo;
    }
}