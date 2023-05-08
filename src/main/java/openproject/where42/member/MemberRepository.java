package openproject.where42.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openproject.where42.group.GroupRepository;
import openproject.where42.groupFriend.GroupFriendRepository;
import openproject.where42.groupFriend.GroupFriend;
import openproject.where42.member.entity.Locate;
import openproject.where42.member.entity.Locations;
import openproject.where42.member.entity.Member;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.Date;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class MemberRepository {

    private final EntityManager em;

    public Long save(Member member) {
        em.persist(member);
        return member.getId();
    }

    /**
     * <pre>
     *  Service 로직에서 Token을 인자로 받아
     *  Member의 id를 검색하고, 해당하는 값을 반환한다.
     * </pre>
     * @author sunghkim
     * @since 1.0
     * @param token42 사용자 고유의 access token
     * @return 토큰을 사용하여 사용자의 DB 고유 ID 값을 봔환, 없다면 0 을 봔환
     */
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


    /**
     * <pre>
     *  Service 로직에서 사용자의 intra id(name)를 인자로 받아
     *  Member를 조회하고 확인하는 함수
     * </pre>
     * @author sunghkim
     * @since 1.0
     * @param name 사용자의 인트라 ID(name)
     * @return 사용자가 존재한다면 Member 객체, 없다면 Null을 반환.
     */
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

    /**
     * <pre>
     *  Service 로직에서 사용자의 intra id(name)을 인자로 받아
     *  Member를 조회하고 확인하는 함수
     * </pre>
     * @author sunghkim
     * @since 1.0
     * @param name 사용자의 인트라 name
     * @return 사용자가 존재한다면 Member DB의 고유 ID, 없다면 Null을 반환.
     */
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


    /**
     * <pre>
     *  Service 로직에서 Member DB의 고유 ID를 인자로 받아
     *  Member를 조회하고 확인하는 함수
     * </pre>
     * @author sunghkim
     * @since 1.0
     * @param id Member DB의 고유 ID
     * @return 사용자가 존재한다면 Member DB의 ID, 없다면 Null을 반환.
     */
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

    /**
     * <pre>
     *  Member의 고유 ID(memberId)와 사용자의 intra 아이디(name)를 인자로 받는다.
     *  memberId는 확인하려는 소유주 ID를 나타내고
     *  name은 그룹에 속하는지 확인하려는 사람의 intra(name)명을 나타낸다.
     *  기본 친구 그룹에서 찾으려는 친구가 존재하는지 확인하는 함수
     * </pre>
     * @author sunghkim
     * @since 1.0
     * @param memberId Member DB의 고유 ID
     * @param name 친구인지 확인하고 싶은 intra 아이디(name)
     * @return 확인하려는 name이 기본그룹에 존재하면 true, 없다면 false
     */
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

    /**
     * <pre>
     *  모든 사용자를 반환하는 함수
     * </pre>
     * @author sunghkim
     * @since 1.0
     * @return 모든 사용자(Member)를 반환, 사용자가 존재하지 않는다면 null 반환
     */
    public List<Member> allMember() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    @Transactional
    public void saveLocateData(String name, Locate locate) {
        Locations sv = new Locations(name, locate,new Date());
        em.persist(sv);
    }
}
