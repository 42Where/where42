package openproject.where42.member.repository;

import lombok.RequiredArgsConstructor;
import openproject.where42.member.domain.Member;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

    private final EntityManager em;

    public void save(Member member) {
        em.persist(member);
    }

    public Member findName(String name) {
        return em.createQuery("SELECT m FROM Member m WHERE m.name = :name", Member.class)
                .setParameter("name", name)
                .getSingleResult();
    }

    public Member findById(Long id) {
        return em.find(Member.class, id);
    }

}
