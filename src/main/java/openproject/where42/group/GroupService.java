package openproject.where42.group;

import lombok.RequiredArgsConstructor;
import openproject.where42.exception.customException.DefaultGroupNameException;
import openproject.where42.exception.customException.DuplicateGroupNameException;
import openproject.where42.exception.customException.NotFoundException;
import openproject.where42.exception.customException.UnregisteredMemberException;
import openproject.where42.member.MemberRepository;
import openproject.where42.member.entity.Member;
import openproject.where42.token.TokenService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;
    private final TokenService tokenService;

    @Transactional
    public Long createDefaultGroup(Member member, String groupName) {
        if (!(groupName.equalsIgnoreCase("기본") || groupName.equalsIgnoreCase("즐겨찾기")))
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

    public Member findOwnerBySessionWithToken(HttpServletRequest req, HttpServletResponse res, String key) {
        String token42 = tokenService.findAccessToken(res, key);
        HttpSession session = req.getSession(false);
        if (session == null) {
            Long memberId = memberRepository.findIdByToken(token42);
            if (memberId == 0)
                throw new UnregisteredMemberException();
            req.getSession();
            session.setAttribute("id", memberId);
        }
        session.setMaxInactiveInterval(60 * 60);
        return memberRepository.findById((Long)session.getAttribute("id"));
    }

    @Transactional
    public void updateGroupName(Long groupId, String groupName) {
        Groups group = groupRepository.findById(groupId);
        if (group == null)
            throw new NotFoundException();
        validateDuplicateGroupName(group.getOwner().getId(), groupName);
        group.updateGroupName(groupName);
    }

    private void validateDuplicateGroupName(Long ownerId, String groupName) {
        if (groupRepository.isGroupNameInOwner(ownerId, groupName))
            throw new DuplicateGroupNameException();
    }

    @Transactional
    public void deleteByGroupId(Long groupId) {
        if (!groupRepository.deleteByGroupId(groupId))
            throw new NotFoundException();
    }
}