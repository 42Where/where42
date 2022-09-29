package openproject.where42.group.repository;

import lombok.RequiredArgsConstructor;
import openproject.where42.group.domain.GroupMember;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class GroupMemberRepository {

    private final EntityManager em;

    public void save(GroupMember groupMember) {
        em.persist(groupMember);
    }

    public void deleteGroupMember(Long id) {
        em.remove(id);
    }

    public void deleteGroupMembers(List<GroupMember> groupMembers) {
        for (GroupMember groupMember : groupMembers) {
            em.remove(groupMember.getId());
        }
    }
}
