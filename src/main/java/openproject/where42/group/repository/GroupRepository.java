package openproject.where42.group.repository;

import lombok.RequiredArgsConstructor;
import openproject.where42.group.domain.Groups;
import openproject.where42.groupFriend.domain.GroupFriend;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.Comparator;
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
        List<GroupFriend> members = em.createQuery("select ms from GroupFriend ms where ms.group.id = :groupId", GroupFriend.class)
                .setParameter("groupId", groupId)
                .getResultList();
        for (GroupFriend member : members) {
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
        List<Groups> groups =  em.createQuery("select gs from Groups gs where gs.owner.id = :ownerId", Groups.class)
                .setParameter("ownerId", ownerId)
                .getResultList();
        for (Groups group : groups) {
            if (group.getGroupName().equals("friends") || group.getGroupName().equals("starred")) {
                groups.remove(group);
            }
        }

        return groups;
    }

    public boolean isGroupNameInOwner(Long ownerId, String groupName) {
        Groups group;
        try {
            group = em.createQuery("select g from Groups g where g.owner.id = :ownerId and g.groupName = :groupName", Groups.class)
                    .setParameter("ownerId", ownerId)
                    .setParameter("groupName", groupName)
                    .getSingleResult();
        } catch (NoResultException e) {
            return false;
        }
        return true;
    }
}
