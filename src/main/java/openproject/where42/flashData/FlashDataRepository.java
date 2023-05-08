package openproject.where42.flashData;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FlashDataRepository {
	private final EntityManager em;
	private final JdbcTemplate jdbcTemplate;


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

	@Transactional
	public void resetFlash() {
		jdbcTemplate.update("delete from flash_data");
	};

}
