package openproject.where42.member.repository;

import lombok.RequiredArgsConstructor;
import openproject.where42.group.domain.Groups;
import openproject.where42.groupMember.domain.GroupMember;
import openproject.where42.member.domain.Member;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

    private final EntityManager em;

    public void save(Member member) {
        em.persist(member);
    }

    public Member findByName(String name) {
        return em.createQuery("SELECT m FROM Member m WHERE m.name = :name", Member.class)
                .setParameter("name", name)
                .getSingleResult();
    }

    public Member findById(Long id) {
        return em.find(Member.class, id);
    }

    public Boolean checkMemberByName(String name) {
        Member member;
        try {
            member = em.createQuery("select m from Member m where m.name = :name", Member.class)
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (NoResultException e) {
            return false;
        }
        return true;
    }

    public Boolean checkFriendByMemberIdAndName(Long memberId, String name) {
        GroupMember friend;
        try {
            friend = em.createQuery("select gm from GroupMember gm where gm.group.owner.id = :memberId and gm.group.groupName = :groupname and gm.friendName = :name", GroupMember.class)
                    .setParameter("memberId", memberId)
                    .setParameter("groupname", "friends")
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (NoResultException e) {
            return false;
        }
        return true;
    }
}
