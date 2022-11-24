package openproject.where42.member;

import lombok.RequiredArgsConstructor;
import openproject.where42.member.entity.FlashMember;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Temporal;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FlashMemberRepository {
	private final EntityManager em;


	/*** DB 저장 ***/
	@Transactional
	public void save(FlashMember flashMember) {
		em.persist(flashMember);
	}

	public FlashMember findByName(String name) {
		try{
			return em.createQuery("select f from FlashMember f where f.name = :name", FlashMember.class)
					.setParameter("name", name)
					.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

}
