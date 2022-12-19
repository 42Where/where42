package openproject.where42.background;

import lombok.RequiredArgsConstructor;
import openproject.where42.api.mapper.Seoul42;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ImageRepository {
	private final EntityManager em;
	private final JdbcTemplate jdbcTemplate;

	public String findByName(String name) {
		try{
			Image result = em.createQuery("select i from Image i where i.name = :name", Image.class)
					.setParameter("name", name)
					.getSingleResult();
			return result.img;
		} catch (NoResultException e) {
			return null;
		}
	}

	@Transactional
	public void deleteMember() {
		jdbcTemplate.update("delete from image A where exists(select * from member B where A.name=B.member_name)");
	}
	@Transactional
	public void deduplication() {
		jdbcTemplate.update("delete from image where active='false'");
		jdbcTemplate.update("delete from image A where exists(select * from member B where A.name=B.member_name)");
		jdbcTemplate.update("delete from image where id in(" +
				"select id from (" +
				"select ROW_NUMBER() over(" +
				"partition by name order by id DESC ) A, " +
				"id from image) B " +
				"where A > 1)");
	}

	@Transactional
	public boolean inputImage(List<Seoul42> list) {
		em.createQuery("delete from Image i");
		int a = 1;
		String sql ="INSERT INTO image (name, img, location, active) VALUES ";
		for (Seoul42 i : list){
			String tmp = "('" +i.getLogin() + "', '" + i.getImage().getLink() + "', '" +
					i.getLocation() + "', '" + i.isActive() + "') ";
			if ( a != list.size())
				tmp += ",";
			sql += tmp;
			a += 1;
		}
		jdbcTemplate.update(sql);
		return true;
	}
}
