package openproject.where42.member;

import lombok.RequiredArgsConstructor;
import openproject.where42.groupFriend.domain.GroupFriend;
import openproject.where42.member.domain.Member;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

    private final EntityManager em;

    public Long save(Member member) {
        em.persist(member);
        return member.getId();
    }

    public Member findByName(String name) {
        Member member;
        try {
            member = em.createQuery("SELECT m FROM Member m WHERE m.name = :name", Member.class)
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
        return member;
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
        GroupFriend friend;
        try {
            friend = em.createQuery("select gm from GroupFriend gm where gm.group.owner.id = :memberId and gm.group.groupName = :groupname and gm.friendName = :name", GroupFriend.class)
                    .setParameter("memberId", memberId)
                    .setParameter("groupname", "기본")
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (NoResultException e) {
            return false;
        }
        return true;
    }
}