package openproject.where42.group.repository;

import lombok.RequiredArgsConstructor;
import openproject.where42.group.domain.Groups;
import openproject.where42.groupMember.domain.GroupMember;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class GroupRepository {

    private final EntityManager em;

    public Long save(Groups groups) {
        em.persist(groups);
        return groups.getId();
    }

    public void deleteByGroupId(Long groupId) {
        List<GroupMember> members = em.createQuery("select ms from GroupMember ms where ms.group.id = :groupId", GroupMember.class)
                .setParameter("groupId", groupId)
                .getResultList();
        for (GroupMember member : members) {
            em.remove(member);
        }
        em.remove(groupId);
    }

    public Groups findById(Long id) {
        return em.find(Groups.class, id);
    }

    public Groups findByOwnerIdAndName(Long ownerId, String name) {
        return em.createQuery("select g from Groups g where g.groupName = :name and g.owner.id = :ownerId", Groups.class)
                .setParameter("ownerId", ownerId)
                .setParameter("name", name)
                .getSingleResult();
    }

    public List<Groups> findGroupsByOwnerId(Long ownerId) {
        return em.createQuery("select gs.groupName from Groups gs where gs.owner.id = :ownerId", Groups.class)
                .setParameter("ownerId", ownerId)
                .getResultList()
                .stream().filter((group) -> !(group.getGroupName().equals("friends") || group.getGroupName().equals("starred")))
                .sorted().collect(Collectors.toList());
    }
}
