package openproject.where42.iamge;

import lombok.RequiredArgsConstructor;
import openproject.where42.api.dto.Seoul42;
import openproject.where42.member.entity.FlashData;
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
	public void deduplication() {
		jdbcTemplate.update("delete from image where active='false'");
		jdbcTemplate.update("delete from test_table A where exists(select * from flash_data B where A.name=B.name)");
		jdbcTemplate.update("delete from test_table A where exists(select * from member B where A.name=B.member_name)");
		jdbcTemplate.update("delete from image where id in(" +
				"select id from (" +
				"select ROW_NUMBER() over(" +
				"partition by name order by id DESC ) A, " +
				"id from image) B " +
				"where A > 1)");
	}

	@Transactional
	public boolean inputImage(List<Seoul42> list) {
		int a = 1;
		String sql ="INSERT INTO flash_data (name, location) VALUES ";
		for (Seoul42 i : list){
			String tmp = "('" +i.getLogin() + "', '" + i.getImage() + "') ";
			if ( a != list.size())
				tmp += ",";
			sql += tmp;
			a += 1;
		}
		jdbcTemplate.update(sql);
		deduplication();
		return true;
	}
}
