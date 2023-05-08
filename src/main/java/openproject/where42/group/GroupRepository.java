package openproject.where42.group;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 그룹 관련 레포지토리
 * @version 1.0
 * @see openproject.where42.group
 */
@Repository
@RequiredArgsConstructor
public class GroupRepository {
    private final EntityManager em;

    public Long save(Groups groups) {
        em.persist(groups);
        return groups.getId();
    }

    /**
     * <pre>
     *  원하는 그룹을 삭제하는 함수
     * </pre>
     * @author sunghkim
     * @since 1.0
     * @param groupId 삭제하고 싶은 그룹의 ID
     * @return 그룹을 성공적으로 삭제하면 true, 삭제하려는 그룹이 없는 그룹이면 false
     */
    public boolean deleteByGroupId(Long groupId) {
        try {
            Groups group = em.createQuery("select g from Groups g where g.id = :groupId", Groups.class)
                    .setParameter("groupId", groupId)
                    .getSingleResult();
            em.remove(group);
            return  true;
        } catch (NoResultException e) {
            return  false;
        }
    }

    /**
     * <pre>
     *  그룹 찾는 함수
     * </pre>
     * @author sunghkim
     * @since 1.0
     * @param id 찾고싶은 그룹의 ID
     * @return 그룹이 존재한다면 Group 객체를, 없다면 null을 반환
     */
    public Groups findById(Long id) {
        try {
            return em.createQuery("select g from Groups g where g.id = :groupId", Groups.class)
                    .setParameter("groupId", id)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * <pre>
     *  멤버의 기본 그룹 및 즐겨찾기 그룹을 제외한 모든 그룹을 조회
     * </pre>
     * @author sunghkim
     * @since 1.0
     * @param ownerId 그룹을 조회하고 싶은 멤버의 DB ID
     * @return 기본, 즐겨찾기 그룹을 제외한 모든 그룹을 리스트 반환
     */
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

    /**
     * <pre>
     *  멤버가 소유하고 있는 그룹인지 확인하는 함수
     * </pre>
     * @author sunghkim
     * @since 1.0
     * @param ownerId 그룹을 조회하고 싶은 멤버의 DB ID
     * @param groupName 존재하는 확인하고 싶은 그룹명
     * @return 기본, 즐겨찾기 그룹을 제외한 모든 그룹을 리스트 반환
     */
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

    public Groups findByOwnerIdAndName(Long ownerId, String name) {
        return em.createQuery("select g from Groups g where g.groupName = :name and g.owner.id = :ownerId", Groups.class)
                .setParameter("ownerId", ownerId)
                .setParameter("name", name)
                .getSingleResult();
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
