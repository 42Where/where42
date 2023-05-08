package openproject.where42.groupFriend;

import lombok.RequiredArgsConstructor;
import openproject.where42.group.Groups;
import openproject.where42.member.entity.Member;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 친구 관련 DB조회 레포지토리
 * @version 1.0
 * @see  openproject.where42.groupFriend
 */
@Repository
@RequiredArgsConstructor
public class GroupFriendRepository {
    private final EntityManager em;

    private final JdbcTemplate jdbcTemplate;
    // 객체 생성
    public Long save(GroupFriend groupFriend) {
        em.persist(groupFriend);
        return groupFriend.getId();
    }

    public List<GroupFriend> findAll(){
        return em.createQuery("select g from GroupFriend g", GroupFriend.class)
                .getResultList();
    }

    /**
     * <pre>
     *  기본그룹에 속한 친구의 이미지 주소 조회 함수
     * </pre>
     * @author sunghkim
     * @since 1.0
     * @param name 이미지 주소를 찾고 싶은 친구 intra 아이디(name)
     * @param defaultGroupId 기본 그룹 ID
     * @return 이미지 URL, 만약 친구가 기본 그룹에 존재하지 않으면 null 반환
     */
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

    /**
     * <pre>
     *  그룹 ID가 유효한 값인지 확인하는 함수
     * </pre>
     * @author sunghkim
     * @since 1.0
     * @param grouId 확인하려는 griup의 ID
     * @return 그룹 ID가 존재한다면 1, 존재하지 않으면 2를 반환
     */
    public int checkGroupId(Long grouId) {
        try {
            em.createQuery("select g.groupName from Groups g where g.id = :id", String.class)
                    .setParameter("id", grouId)
                    .getSingleResult();
            return 1;
        } catch (NoResultException e) {
            return 2;
        }
    }

    /**
     * <pre>
     *  해당 그룹에 포함되지 않는 친구 목록 front 반환
     *  없는 그룹인지 확인하며, 존재하는 그룹에 대해서만 진행
     *  기본 그룹에 있는 모든 친구들을 리스트화 하고,
     *  해당 groupId에 있는 모든 친구들도 리스트화 한다.
     *  해당 그룹에 이미 있는 친구들을 제외하고 등록되지 않은 친구 반환
     * </pre>
     * @author sunghkim
     * @since 1.0
     * @param member 그룹의 소유 멤버
     * @param groupId 확인하려는 griup의 ID
     * @return 그룹 ID가 존재한다면 1, 존재하지 않으면 2를 반환
     */
    public List<String> notIncludeFriendByGroup(Member member, Long groupId){
        if (checkGroupId(groupId) == 2)
            return null;
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

    /**
     * <pre>
     *  친구가 해당된 모든 그룹에서 삭제하는 함수
     *  해당 member의 모든 그룹을 가져온다.
     *  모든 그룹을 순회하며, 친구를 모든 그룹에서 삭제한다
     *  친구가 이미 없는 그룹은 그냥 넘어가게 된다
     * </pre>
     * @author sunghkim
     * @since 1.0
     * @param member 그룹의 소유 멤버
     * @param friendName 삭제하려는 친구 intra 아이디
     * @return 오직 true만 반환
     */
    public boolean deleteFriendByFriendName(Member member, String friendName) {
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
        return true;
    }

    /**
     * <pre>
     *  해당 그룹에 속한 모든 친구들을 반환
     *  그룹이 존재하는 여부도 확인
     * </pre>
     * @author sunghkim
     * @since 1.0
     * @param groupId 조회하려는 그룹 ID
     * @return 해당 그룹의 모든 친구들 반환, 그룹이 없거나 친구가 없으면 null 반환
     */
    public List<String> findGroupFriendsByGroupId(Long groupId) {
        if (checkGroupId(groupId) == 2)
            return null;
        List<String> result = em.createQuery("select gm.friendName from GroupFriend gm where gm.group.id = :groupId", String.class)
                                .setParameter("groupId", groupId)
                                .getResultList()
                                .stream().sorted()
                                .collect(Collectors.toList());
        return result;
    }

    /**
     * <pre>
     *  member의 기본 그룹에 있는 친구를 모두 반환
     *  알파벳 이름순으로 정렬해서 반환
     * </pre>
     * @author sunghkim
     * @since 1.0
     * @param ownerId Member DB에서 찾으려는 member의 ID
     * @return member의 기본 그룹에 속한 모든 친구들 반환, 그룹이 없거나 친구가 없으면 빈 리스트 반환
     */
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

    /**
     * <pre>
     *  삭제하려는 친구들의 이름 리스트를 받아 삭제
     *  만약 삭제하려는 친구가 그룹에 없다면 그냥 넘어감
     * </pre>
     * @author sunghkim
     * @since 1.0
     * @param groupId 친구를 삭제하려는 그룹
     * @param friendNames 삭제하려는 친구들의 리스트
     * @return 오직 true만 반환
     */
    public boolean deleteGroupFriends(Long groupId, List<String> friendNames) {
        for (String friendName : friendNames) {
            try {
                GroupFriend friend = em.createQuery("select gf from GroupFriend gf where gf.group.id = :groupId and gf.friendName = :friendName", GroupFriend.class)
                        .setParameter("groupId", groupId)
                        .setParameter("friendName", friendName)
                        .getSingleResult();
                em.remove(friend);
            }
            catch (NoResultException e) {}
        }
        return true;
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
}