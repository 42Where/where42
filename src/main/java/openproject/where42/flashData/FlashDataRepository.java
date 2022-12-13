package openproject.where42.flashData;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FlashDataRepository {
	private final EntityManager em;


	/*** DB 저장 ***/
	@Transactional
	public void save(FlashData flashData) {
		em.persist(flashData);
	}

	public FlashData findByName(String name) {
		try{
			return em.createQuery("select f from FlashData f where f.name = :name", FlashData.class)
					.setParameter("name", name)
					.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

}
