package openproject.where42.group;

import lombok.RequiredArgsConstructor;
import openproject.where42.exception.DefaultGroupNameException;
import openproject.where42.exception.DuplicateGroupNameException;
import openproject.where42.exception.SessionExpiredException;
import openproject.where42.group.domain.Groups;
import openproject.where42.member.MemberRepository;
import openproject.where42.member.domain.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Long createDefaultGroup(Member member, String groupName) {
        if (!(groupName.equalsIgnoreCase("기본") || groupName.equalsIgnoreCase("즐겨찾기"))) // 기본이나 즐겨찾기 아닌 다른 그룹 혹시 잘못 호출 시 예외 터트릴 건데 어디서 잡을 지 모르겠어서 일단 걍 리턴 나중에 에러처리 필요
            throw new DefaultGroupNameException();
        return groupRepository.save(new Groups(groupName, member));
    }

    @Transactional
    public Long createCustomGroup(String groupName, Member owner) {
        validateDuplicateGroupName(owner.getId(), groupName);
        return groupRepository.save(new Groups(groupName, owner));
    }

    public List<Groups> findAllGroupsExceptDefault(Long ownerId) {
        return groupRepository.findGroupsByOwnerId(ownerId);
    }

    public Member findOwnerBySession(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null)
            throw new SessionExpiredException();
        return memberRepository.findById((Long)session.getAttribute("id"));
    }

    @Transactional
    public void updateGroupName(Long groupId, String groupName) {
        Groups group = groupRepository.findById(groupId);
        validateDuplicateGroupName(group.getOwner().getId(), groupName);
        group.updateGroupName(groupName);
    }

    private void validateDuplicateGroupName(Long ownerId, String groupName) { // 여러곳에서 호출 시에 대한 에러 처리 필요 싱글톤 패턴 참고
        if (groupRepository.isGroupNameInOwner(ownerId, groupName))
            throw new DuplicateGroupNameException();
    }

    @Transactional
    public void deleteByGroupId(Long groupId) {
        groupRepository.deleteByGroupId(groupId);
    }
}