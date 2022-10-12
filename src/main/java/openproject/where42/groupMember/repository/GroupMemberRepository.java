package openproject.where42.groupMember.repository;

import lombok.RequiredArgsConstructor;
import openproject.where42.group.domain.Groups;
import openproject.where42.groupMember.domain.GroupMember;
import openproject.where42.member.domain.Member;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class GroupMemberRepository {

    private final EntityManager em;

    // 객체 생성
    public Long save(GroupMember groupMember) {
        em.persist(groupMember);
        return groupMember.getId();
    }

    // 다중 친구 그룹 추가
    public void multiSave(List<GroupMember> groupMembers) {
        for (GroupMember groupMember : groupMembers) {
            em.persist(groupMember);
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
                GroupMember member2 = em.createQuery("select gm from GroupMember gm where gm.friendName = :friendName and gm.group = :group", GroupMember.class)
                        .setParameter("friendName", friendName)
                        .setParameter("group", group)
                        .getSingleResult();
            } catch (NoResultException e) {
                result.add(group.getGroupName());
            }
        }
        return result;
    }

    // 해당 그룹에 포함되지 않는 친구 목록 front 반환
    public List<String> notIncludeFriendByGroup(Member member, Long groupId) {
        List<GroupMember> friends = em.createQuery("select gs from GroupMember gs where gs.group.owner = :member and gs.group.groupName = :groupName", GroupMember.class)
                .setParameter("member", member)
                .setParameter("groupName", "friends")
                .getResultList();
        List<GroupMember> groupMembers = em.createQuery("select gs from GroupMember gs where gs.group.owner = :member and gs.group.id = :groupId", GroupMember.class)
                .setParameter("member", member)
                .setParameter("groupId", groupId)
                .getResultList();
        Map<String, Boolean> groupMap = new HashMap<>();
        for (GroupMember groupMember : groupMembers) {
            groupMap.put(groupMember.getFriendName(), true);
        }
        List<String> result = new ArrayList<>();
        for (GroupMember friend : friends) {
            if (groupMap.get(friend.getFriendName()) == null)
                result.add(friend.getFriendName());
        }
        return result;
    }

    // 한명 삭제 그룹 멤버 DB 삭제
    public void deleteGroupMember(GroupMember groupMember) {
        em.remove(groupMember);
    }

    // 다중 친구 그룹 삭제
    public void deleteGroupMembers(List<GroupMember> groupMembers) {
        for (GroupMember groupMember : groupMembers) {
            em.remove(groupMember.getId());
        }
    }

    // 친구가 해당된 모든 그룹에서 삭제하기
    public void deleteFriendsGroupByName(Member member, String friendName) {
        List<Groups> groups = em.createQuery("select g from Groups g where g.owner = :member", Groups.class)
                .setParameter("member", member)
                .getResultList();
        for (Groups group : groups) {
            GroupMember groupMember = em.createQuery("select gm from GroupMember gm where gm.group = :group and gm.friendName = :friendName", GroupMember.class)
                    .setParameter("group", group)
                    .setParameter("friendName", friendName)
                    .getSingleResult();
            if (groupMember != null)
                em.remove(groupMember);
        }
    }

    // 그룹 속하는 멤버 리스트 리턴
    public List<String> findGroupMembersByGroupId(Long groupId) {
        return em.createQuery("select gm from GroupMember gm where gm.group.id = :groupId", GroupMember.class)
                .setParameter("groupId", groupId)
                .getResultList()
                .stream().map((groupMember) -> groupMember.getFriendName())
                .sorted()
                .collect(Collectors.toList());
    }

    public List<String> findAllGroupMemberByOwnerId(Long ownerId) {
        Groups group = em.createQuery("select g from Groups g where g.owner.id = :ownerId and g.groupName = :groupName", Groups.class)
                .setParameter("groupName", "friends")
                .setParameter("ownerId", ownerId)
                .getSingleResult();
        return em.createQuery("select gm.friendName from GroupMember gm where gm.group = :group", GroupMember.class)
                .setParameter("group", group)
                .getResultList()
                .stream().map(groupMember -> groupMember.getFriendName())
                .sorted()
                .collect(Collectors.toList());
    }
}
