package openproject.where42.member;

import lombok.RequiredArgsConstructor;
import openproject.where42.group.GroupService;
import openproject.where42.group.domain.Groups;
import openproject.where42.group.repository.GroupRepository;
import openproject.where42.groupFriend.GroupFriendService;
import openproject.where42.groupFriend.domain.GroupFriendInfo;
import openproject.where42.member.domain.Locate;
import openproject.where42.member.domain.Member;
import openproject.where42.member.dto.MemberGroupInfo;
import openproject.where42.member.repository.MemberRepository;
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

    @Transactional
    public void createMember(Member member) {
        memberRepository.save(member);
    }

    private final GroupRepository groupRepository;

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

    public List<MemberGroupInfo> findAllGroupFriendsInfo(Long memberId) {
        List<MemberGroupInfo> groupList = new ArrayList<MemberGroupInfo>();
        List<Groups> groups = groupService.findGroups(memberId);
        Member member = memberRepository.findById(memberId);

        groupList.add(new MemberGroupInfo(groupRepository.findById(member.getStarredGroupId()),
                groupFriendService.findAllGroupFriendNameByGroupId(member.getStarredGroupId())));
        for (Groups g : groups) // 그룹하나 당 groupInfo 만들어서 리스트에 추가해서 반환
            groupList.add(new MemberGroupInfo(g, groupFriendService.findAllGroupFriendNameByGroupId(g.getId())));
        groupList.add(new MemberGroupInfo(groupRepository.findById(member.getDefaultGroupId()),
                groupFriendService.findAllGroupFriendNameByGroupId(member.getDefaultGroupId())));
        return groupList;
    }

    public List<GroupFriendInfo> findAllFriendsInfo(Long memberId) {
        Member member = memberRepository.findById(memberId);
//        return groupFriendService.findAllFriendsInfo(member.getDefaultGroup()); // 기본 그룹 id 보내주면 기본 그룹에 있는 친구들 정보 싹다 정리해서 반환
        return null;
    }
}