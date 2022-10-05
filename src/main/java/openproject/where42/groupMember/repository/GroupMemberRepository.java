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

@Repository
@RequiredArgsConstructor
public class GroupMemberRepository {

    private final EntityManager em;

    // 객체 생성
    public void save(GroupMember groupMember) {
        em.persist(groupMember);
    }

    // 다중 친구 그룹 추가
    public void multiSave(List<GroupMember> groupMembers) {
        for (GroupMember groupMember : groupMembers) {
            em.persist(groupMember);
        }
    }

    // 해당 친구가 포함되지 않은 그룹 목록 front 반환
    public List<String> notIncludeGroupByFriend(Member member, GroupMember groupMember) {
        List<Groups> groups = em.createQuery("select g from Groups g where g.owner = :member", Groups.class)
                .setParameter("member", member)
                .getResultList();
        List<String> result = new ArrayList<>();
        for (Groups group : groups) {
            try {
                GroupMember member2 = em.createQuery("select gm from GroupMember gm where gm.friendName = :groupMember and gm.group = :group", GroupMember.class)
                        .setParameter("groupMember", groupMember.getFriendName())
                        .setParameter("group", group)
                        .getSingleResult();
            } catch (NoResultException e) {
                result.add(group.getGroupName());
            }
        }
        return result;
    }

    // 해당 그룹에 포함되지 않는 친구 목록 front 반환
    public List<String> notIncludeFriendByGroup(Member member, Groups group) {
        List<GroupMember> friends = em.createQuery("select gs from GroupMember gs where gs.group.owner = :member and gs.group.groupName = :group", GroupMember.class)
                .setParameter("member", member)
                .setParameter("group", "friends")
                .getResultList();
        List<GroupMember> groupMembers = em.createQuery("select gs from GroupMember gs where gs.group.owner = :member and gs.group = :group", GroupMember.class)
                .setParameter("member", member)
                .setParameter("group", group)
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
    public void deleteFriendsGroupByName(Member member, String name) {
        List<Groups> groups = em.createQuery("select g from Groups g where g.owner = :member", Groups.class)
                .setParameter("member", member)
                .getResultList();
        for (Groups group : groups) {
            GroupMember groupMember = em.createQuery("select gm from GroupMember gm where gm.group = :group and gm.friendName = :name", GroupMember.class)
                    .setParameter("group", group)
                    .setParameter("name", name)
                    .getSingleResult();
            if (groupMember != null)
                em.remove(groupMember);
        }
    }
}
