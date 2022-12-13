package openproject.where42.groupFriend;

import lombok.RequiredArgsConstructor;
import openproject.where42.group.Groups;
import openproject.where42.member.entity.Member;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class GroupFriendRepository {
    private final EntityManager em;

    // 객체 생성
    public Long save(GroupFriend groupFriend) {
        em.persist(groupFriend);
        return groupFriend.getId();
    }

    public String findImageById(String name, Long defaultGroupId){
        try {
            return em.createQuery("select g.img from GroupFriend g where g.friendName = :name and g.group.id = :id", String.class)
                    .setParameter("name", name)
                    .setParameter("id", defaultGroupId)
                    .getSingleResult();
        } catch (NoResultException e) {
            System.out.println("======= error [findImageById] =======");
            return null;
        }
    }
    // 해당 친구가 포함되지 않은 그룹 목록 front 반환
    public List<String> notIncludeGroupByMemberAndFriendName(Member member, String friendName) {
        List<Groups> groups = em.createQuery("select g from Groups g where g.owner = :member", Groups.class)
                .setParameter("member", member)
                .getResultList();
        List<String> result = new ArrayList<>();
        for (Groups group : groups) {
            try {
                GroupFriend member2 = em.createQuery("select gm from GroupFriend gm where gm.friendName = :friendName and gm.group = :group", GroupFriend.class)
                        .setParameter("friendName", friendName)
                        .setParameter("group", group)
                        .getSingleResult();
            } catch (NoResultException e) {
                result.add(group.getGroupName());
            }
        }
        return result.stream()
                .sorted()
                .collect(Collectors.toList());
    }

    // 해당 그룹에 포함되지 않는 친구 목록 front 반환
    public List<String> notIncludeFriendByGroup(Member member, Long groupId) {
        List<GroupFriend> friends = em.createQuery("select gs from GroupFriend gs where gs.group.owner = :member and gs.group.groupName = :groupName", GroupFriend.class)
                .setParameter("member", member)
                .setParameter("groupName", "기본")
                .getResultList();
        List<GroupFriend> groupFriends = em.createQuery("select gs from GroupFriend gs where gs.group.owner = :member and gs.group.id = :groupId", GroupFriend.class)
                .setParameter("member", member)
                .setParameter("groupId", groupId)
                .getResultList();
        Map<String, Boolean> groupMap = new HashMap<>();
        for (GroupFriend groupFriend : groupFriends) {
            groupMap.put(groupFriend.getFriendName(), true);
        }
        List<String> result = new ArrayList<>();
        for (GroupFriend friend : friends) {
            if (groupMap.get(friend.getFriendName()) == null)
                result.add(friend.getFriendName());
        }
        Collections.sort(result, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        return result;
    }

    // 한명 삭제 그룹 멤버 DB 삭제
    public void deleteGroupFriendByGroupFriendId(Long groupFriendId) {
        int result = em.createQuery("delete from GroupFriend gs where gs.id = :groupFriendId")
                .setParameter("groupFriendId", groupFriendId)
                .executeUpdate();
    }

    // 다중 친구 그룹 삭제
    public void deleteGroupFriendsByGroupFriendId(List<Long> groupFriendIds) {
        for (Long groupFriendId : groupFriendIds) {
            em.createQuery("delete from GroupFriend gs where gs.id = :groupFriendId")
                    .setParameter("groupFriendId", groupFriendId)
                    .executeUpdate();
        }
    }

    // 친구가 해당된 모든 그룹에서 삭제하기 deleteFriendsGroupByName -> deleteFriendByFriendName
    public void deleteFriendByFriendName(Member member, String friendName) {
        List<Groups> groups = em.createQuery("select g from Groups g where g.owner = :member", Groups.class)
                .setParameter("member", member)
                .getResultList();
        for (Groups group : groups) {
            GroupFriend groupFriend;
            try {
                groupFriend = em.createQuery("select gm from GroupFriend gm where gm.group = :group and gm.friendName = :friendName", GroupFriend.class)
                        .setParameter("group", group)
                        .setParameter("friendName", friendName)
                        .getSingleResult();
            } catch (NoResultException e) {
                groupFriend = null;
            }
            if (groupFriend != null)
                em.remove(groupFriend);
        }
    }

    // 그룹 속하는 멤버 리스트 리턴
    public List<String> findGroupFriendsByGroupId(Long groupId) {
        return em.createQuery("select gm.friendName from GroupFriend gm where gm.group.id = :groupId", String.class)
                .setParameter("groupId", groupId)
                .getResultList()
                .stream().sorted()
                .collect(Collectors.toList());
    }

    public List<GroupFriend> findAllGroupFriendByOwnerId(Long ownerId) {
        Groups groups;
        try {
                Groups group = em.createQuery("select g from Groups g where g.id = :ownerId and g.groupName = :groupName", Groups.class)
                        .setParameter("groupName", "기본")
                        .setParameter("ownerId", ownerId)
                        .getSingleResult();
                List<GroupFriend> friends = em.createQuery("select gm from GroupFriend gm where gm.group = :group", GroupFriend.class)
                        .setParameter("group", group)
                        .getResultList();
                Collections.sort(friends, new Comparator<GroupFriend>() {
                    @Override
                    public int compare(GroupFriend o1, GroupFriend o2) {
                        return o1.getFriendName().compareTo(o2.getFriendName());
                    }
                });
                return friends;
        } catch (NoResultException e) {
            return new ArrayList<GroupFriend>();
        }
    }

    public void deleteGroupFriends(Long groupId, List<String> friendNames) {
        for (String friendName : friendNames) {
            GroupFriend friend = em.createQuery("select gf from GroupFriend gf where gf.group.id = :groupId and gf.friendName = :friendName", GroupFriend.class)
                    .setParameter("groupId", groupId)
                    .setParameter("friendName", friendName)
                    .getSingleResult();
            em.remove(friend);
        }
    }
}