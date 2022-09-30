package openproject.where42.groupMember.repository;

import lombok.RequiredArgsConstructor;
import openproject.where42.groupMember.domain.GroupMember;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class GroupMemberRepository {

    private final EntityManager em;

    public void save(GroupMember groupMember) {
        em.persist(groupMember);
    }

    public void deleteGroupMember(GroupMember groupMember) {
        em.remove(groupMember);
    }

    public void deleteGroupMembers(List<GroupMember> groupMembers) {
        for (GroupMember groupMember : groupMembers) {
            em.remove(groupMember.getId());
        }
    }
}
