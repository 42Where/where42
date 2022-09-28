package openproject.where42.group;

import lombok.RequiredArgsConstructor;
import openproject.where42.group.domain.Groups;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
@RequiredArgsConstructor
public class GroupRepository {

    private final EntityManager em;

    public void save(Groups groups) {
        em.persist(groups);
    }
}
