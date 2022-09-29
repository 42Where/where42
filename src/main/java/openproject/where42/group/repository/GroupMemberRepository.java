package openproject.where42.group.repository;

import lombok.RequiredArgsConstructor;
import openproject.where42.group.domain.GroupMember;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
@RequiredArgsConstructor
public class GroupMemberRepository {

    private final EntityManager em;

    public void save(GroupMember groupMember) {
        em.persist(groupMember);
    }
}
