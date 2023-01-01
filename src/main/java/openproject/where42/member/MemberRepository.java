package openproject.where42.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openproject.where42.group.GroupRepository;
import openproject.where42.groupFriend.GroupFriendRepository;
import openproject.where42.groupFriend.GroupFriend;
import openproject.where42.member.entity.Administrator;
import openproject.where42.member.entity.Member;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class MemberRepository {

    private final EntityManager em;
    private final GroupRepository groupRepository;
    private final GroupFriendRepository groupFriendRepository;

    public Long save(Member member) {
        em.persist(member);
        return member.getId();
    }

    public Long findIdByToken(String token42) {
        try {
            String name = em.createQuery("select t.memberName from Token t where t.accessToken = :token", String.class)
                    .setParameter("token", token42)
                    .getSingleResult();
            return em.createQuery("select m.id from Member m where m.name = :name", Long.class)
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (NoResultException e) {
            return Long.valueOf(0);
        }
    }

    public Member findByName(String name) {
        try {
            Member member = em.createQuery("SELECT m FROM Member m WHERE m.name = :name", Member.class)
                    .setParameter("name", name)
                    .getSingleResult();
            return member;
        } catch (NoResultException e) {
            return null;
        }
    }

    public Long findId(String name) {
        try {
            Member member = em.createQuery("SELECT m FROM Member m WHERE m.name = :name", Member.class)
                    .setParameter("name", name)
                    .getSingleResult();
            return member.getId();
        } catch (NoResultException e) {
            return null;
        }
    }


    public Member findById(Long id) {
        Member member;
        try {
            member = em.createQuery("SELECT m FROM Member m WHERE m.id = :id", Member.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
        return member;
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

    @Transactional
    public void deleteMember(String name){
        System.out.println("============== excute delete query =============");
        em.remove(em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getSingleResult());
    }

    public List<Member> allMember() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }
}