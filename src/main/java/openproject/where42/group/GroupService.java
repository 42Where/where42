package openproject.where42.group;

import lombok.RequiredArgsConstructor;
import openproject.where42.group.domain.GroupMember;
import openproject.where42.group.domain.Groups;
import openproject.where42.group.repository.GroupRepository;
import openproject.where42.member.repository.MemberRepository;
import openproject.where42.member.domain.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;

    //생성 로직
    @Transactional // 기본적으로 false여서 안쓰면 false임
    public Long saveGroup(String groupName, Long ownerId) {
        Member owner = memberRepository.findById(ownerId);
        Groups group = new Groups(groupName, owner); // 이렇게 하면 id가 자동으로 들어갈지?
        validateDuplicateMember(group);
        //groupRepository.save(group);
        return group.getId();
    }

    private void validateDuplicateMember(Groups group) {
        String groupName = group.getGroupName();

        if (groupName == "default" || groupName == "starred") // default? 기본?
            throw new IllegalStateException("사용할 수 없는 그룹 이름입니다.");
        if (group.getOwner().findGroupName(groupName))
            throw new IllegalStateException("이미 사용하고 있는 그룹 이름입니다.");
    }

    //삭제 로직
    @Transactional
    public void deleteGroup(Long groupId) {
        Groups g = groupRepository.findById(groupId);

        for (GroupMember f : g.getGroupMembers()){
            // 그룹 친구 하나씩 다 지우는 로직 필요 쿼리를 하나씩날리나? 아니면 배열로 날려도 되나?
        }
        groupRepository.deleteGroup(g); // 그룹 객체를 직접 보내는 게 낫나 아이디를 보내는게 낫나 뭐가 빠른지 모르겠음
    }
}
