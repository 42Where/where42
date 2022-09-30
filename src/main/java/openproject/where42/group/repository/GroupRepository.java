package openproject.where42.group.repository;

import lombok.RequiredArgsConstructor;
import openproject.where42.group.domain.Groups;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

@Repository
@RequiredArgsConstructor
public class GroupRepository {

    private final EntityManager em;

    public void save(Groups groups) {
        em.persist(groups);
    }

    public void deleteGroup(Groups group) {
        em.remove(group);
    }

    public Groups findById(Long id) {
        return em.find(Groups.class, id);
    }

    public Groups findByName(String name) {
        return em.createQuery("select g from Groups g where g.groupName = :name", Groups.class)
                .setParameter("name", name)
                .getSingleResult();
    }
}
