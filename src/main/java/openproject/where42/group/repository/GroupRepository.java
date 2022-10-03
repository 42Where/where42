package openproject.where42.group.repository;

import lombok.RequiredArgsConstructor;
import openproject.where42.group.domain.Groups;
import openproject.where42.groupMember.domain.GroupMember;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class GroupRepository {

    private final EntityManager em;

    public void save(Groups groups) {
        em.persist(groups);
    }

    public void deleteGroup(Groups group) {
        List<GroupMember> members = em.createQuery("select ms from GroupMember ms where ms.group = :group", GroupMember.class)
                .setParameter("group", group)
                .getResultList();
        for (GroupMember member : members) {
            em.remove(member);
        }
        em.remove(group);
    }

    public Groups findById(Long id) {
        return em.find(Groups.class, id);
    }

    public Groups findByName(String name) {
        return em.createQuery("select g from Groups g where g.groupName = :name", Groups.class)
                .setParameter("name", name)
                .getSingleResult();
    }

    public List<String> findGroupsByOwnerName(String name) {
        return em.createQuery("select gs.groupName from Groups gs where gs.owner = :name", String.class)
                .setParameter("name", name)
                .getResultList();
    }
}
