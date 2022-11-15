package openproject.where42.group;

import lombok.RequiredArgsConstructor;
import openproject.where42.group.domain.Groups;
import openproject.where42.groupFriend.domain.GroupFriend;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.Collections;
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

    // em.remove는 id로 동작안함
    public void deleteByGroupId(Long groupId) {
        List<GroupFriend> members = em.createQuery("select ms from GroupFriend ms where ms.group.id = :groupId", GroupFriend.class)
                .setParameter("groupId", groupId)
                .getResultList();
        for (GroupFriend member : members) {
            System.out.println(member.getFriendName());
            em.remove(member);
        }
        Groups group = em.createQuery("select g from Groups g where g.id = :groupId", Groups.class)
                .setParameter("groupId", groupId)
                .getSingleResult();
        em.remove(group);
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
        List<Groups> groups =  em.createQuery("select gs from Groups gs where gs.owner.id = :ownerId and gs.groupName not in (:friends, :starred)", Groups.class)
                .setParameter("ownerId", ownerId)
                .setParameter("friends", "기본")
                .setParameter("starred", "즐겨찾기")
                .getResultList();
        Collections.sort(groups, new Comparator<Groups>() {
            @Override
            public int compare(Groups o1, Groups o2) {
                return o1.getGroupName().compareTo(o2.getGroupName());
            }
        });
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

    public Groups findGroupsByOwnerIdAndGroupNames(Long ownerId, String groupName) {
        Groups group = em.createQuery("select g from Groups g where g.owner.id = :ownerId and g.groupName = :groupName", Groups.class)
                .setParameter("ownerId", ownerId)
                .setParameter("groupName", groupName)
                .getSingleResult();
        return group;
    }

    public List<Groups> findGroupsByOwnerIdAndGroupNames(Long ownerId, List<String> groupNames) {
        List<Groups> groups = new ArrayList<>();
        for (String groupName : groupNames) {
            Groups group = em.createQuery("select g from Groups g where g.owner.id = :ownerId and g.groupName = :groupName", Groups.class)
                    .setParameter("ownerId", ownerId)
                    .setParameter("groupName", groupName)
                    .getSingleResult();
            groups.add(group);
        }
        Collections.sort(groups, new Comparator<Groups>() {
            @Override
            public int compare(Groups o1, Groups o2) {
                return o1.getGroupName().compareTo(o2.getGroupName());
            }
        });
        return groups;
    }
}
