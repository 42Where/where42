package openproject.where42.background;

import lombok.RequiredArgsConstructor;
import openproject.where42.api.mapper.Seoul42;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.List;

/**
 * flash 레포지토리
 * @version 1.0
 * @see openproject.where42.background
 */
@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ImageRepository {
	private final EntityManager em;
	private final JdbcTemplate jdbcTemplate;

	/**
	 * <pre>
	 *  intra 아이디(name) 값으로 img URL 을 찾아서 반환하는 함수
	 * </pre>
	 * @author sunghkim
	 * @since 1.0
	 * @param name 찾으려는 img의 소유주 intra 아이디(name)
	 * @return img가 존재하면 URL반환, 없다면 null 반환
	 */
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

	/**
	 * <pre>
	 *  imageDB 중복 검사 함수
	 *  active(블랙홀) 여부 판단 후 삭제
	 *  혹여 member가 된 인원이 있다면 테이블에서 삭제
	 *  이름이 같은 이미지들 중 가장 최근의 이미지 정보만 남기고 삭제
	 * </pre>
	 * @author sunghkim
	 * @since 1.0
	 */
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

	/**
	 * <pre>
	 *  image 테이블에 img 정보 및 사용자 정보를
	 *  직접 쿼리문을 작성하여 넣는 함수.
	 *  굳이 이렇게 한 이유는 JDBC의 batch insert가 안먹혀서 작성하게 됨
	 * </pre>
	 * @author sunghkim
	 * @since 1.0
	 * @param list image 테이블에 기입할 사용자들 리스트
	 * @return 오직 true만 반환
	 */
	@Transactional
	public boolean inputImage(List<Seoul42> list) {
		int a = 1;

		em.createQuery("delete from Image i");
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
