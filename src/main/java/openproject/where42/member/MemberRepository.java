package openproject.where42.member;

import lombok.RequiredArgsConstructor;
import openproject.where42.group.domain.Groups;
import openproject.where42.member.domain.Member;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

    private final EntityManager em;

    public void save(Member member) {
        em.persist(member);
    }
    public Member findOne(String name) {
        // 이름으로 멤버 찾기. jpashop은 id로 찾던데 우리도 그게 가능한지.. id를 어떻게 받는 지 진짜 모르겠음
        // 근데 무조건 id로 찾아야 하는게, JPA에서 Entity관리할 때 id를 키로 find를 수행해서 어떻게든 id로 받는 방법을 찾아야할듯?

        return em.find(Member.class, name);
    }

    public Member findById(Long id) {
        return null;
    }
}
