package openproject.where42.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openproject.where42.api.mapper.OAuthToken;
import openproject.where42.member.entity.Administrator;
import openproject.where42.token.Token;
import openproject.where42.token.TokenRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.Date;
import java.util.List;

/**
 * admin 토큰 DB를 조회하는 레포지토리
 * @version 1.0
 * @see  openproject.where42.token
 */
@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AdminRepository {
	private final EntityManager em;
	private final AdminApiService adminApiService;

	/**
	 * <pre>
	 *  관리자의 토큰을 저장하는 함수
	 * </pre>
	 * @author sunghkim
	 * @since 1.0
	 * @param name 저장할 관리자 ID
	 * @param oAuthToken 저장할 토큰
	 * @return 저장한 뒤 key값이 될 uuid 반환
	 */
	@Transactional
	public String saveAdmin(String name , OAuthToken oAuthToken) {
		Token token = new Token(
				name,
				oAuthToken.getAccess_token(),
				oAuthToken.getRefresh_token());
		em.persist(token);
		return token.getUUID();
	}

	public String callAdminSecret(){
		try {
			Token token =  em.createQuery("select m from Token m where m.memberName = :name", Token.class)
					.setParameter("name", "admin_secret")
					.getSingleResult();
			return token.getAccessToken();
		} catch (NoResultException e) {
			return null;
		}
	}

	public String callAdmin() {
		try {
			Token token =  em.createQuery("select m from Token m where m.memberName = :name", Token.class)
					.setParameter("name", "admin")
					.getSingleResult();
			checkToken(token);
			return token.getAccessToken();
		} catch (NoResultException e) {
			return null;
		}
	}

	public String callHane() {
		try {
			Token token = em.createQuery("select m from Token m where m.memberName = :name", Token.class)
					.setParameter("name", "hane")
					.getSingleResult();
			return token.getAccessToken();
		} catch (NoResultException e) {
			return null;
		}
	}

	@Transactional
	public void insertHane(String token) {
		try {
			Token hane = em.createQuery("select t from Token t where t.memberName= :name", Token.class)
					.setParameter("name", "hane")
					.getSingleResult();
			hane.updateAccess(token);
		} catch (NoResultException e) {
			Token hane = new Token("hane", token, "null");
			em.persist(hane);
		}
	}

	/**
	 * <pre>
	 *  토큰의 상태가 사용할 수 있는지 유효성 검사
	 *  발급받은 시점으로부터 60분이 자났다면 새로 발급
	 * </pre>
	 * @author sunghkim
	 * @since 1.0
	 * @param token 유효성을 확인하고 싶은 Token값
	 */
	@Transactional
	public void checkToken(Token token) {
		Date now = new Date();
		Long diff = (now.getTime() - token.getRecentLogin().getTime()) / 60000;
		log.info("[checkToken] Token을 발급받은지 {}분 지났습니다.", diff);
		if (diff > 60){
			log.info("[checkToken] 시간이 경과하여 새로운 Token을 발급받습니다\n ========= 기존 토큰 =========\n{}", token.getAccessToken());
			OAuthToken oAuthToken = adminApiService.getAdminNewOAuthToken(callAdminSecret() ,token.getRefreshToken());
			token.updateAccess(oAuthToken.getAccess_token());
			token.updateRefresh(oAuthToken.getRefresh_token());
			log.info("[checkToken] ========= 발급받은 토큰 =========\n{}", token.getAccessToken());
		}
	}

	@Transactional
	public void insertAdminSecret(String secret) {
		try {
			Token tmp = em.createQuery("select t from Token t where t.memberName= :name", Token.class)
					.setParameter("name", "admin_secret")
					.getSingleResult();
			tmp.updateAccess(secret);
		} catch (NoResultException e) {
			Token tmp = new Token("admin_secret", secret, "null");
			em.persist(tmp);
		}
	}

	/**
	 * <pre>
	 *  관리자 계정의 유효성을 확인하는 함수.
	 *  사용자의 아이디(name)과 암호(passwd)를 확인한다.
	 * </pre>
	 * @author sunghkim
	 * @since 1.0
	 * @param name 로그인하려는 admin 아이디
	 * @param passwd 로그인하려는 admin 암호
	 * @return admin의 계정이 맞다면 true, 틀리다면 false를 반환한다.
	 */
	public boolean adminLogin(String name, String passwd) {
		try {
			em.createQuery("select a.id from Administrator a where a.name = :name and a.passwd = :passwd", Long.class)
					.setParameter("name", name)
					.setParameter("passwd", passwd)
					.getSingleResult();
			log.info("[admin] {} 님이 로그인하셨습니다.", name);
			return true;
		} catch (NoResultException e) {
			log.info("[admin] 아이디 또는 비밀번호가 일치하지 않습니다.");
			return false;
		}
	}

	/**
	 * <pre>
	 *  모든 관리자를 조회한다.
	 * </pre>
	 * @author sunghkim
	 * @since 1.0
	 * @return 모든 관리자를 조회하여 list로 반환, 관리자가 없다면 null 반환
	 */
	public List<Administrator> findAllAdmin() {
		try {
			return em.createQuery("select a from Administrator a", Administrator.class)
					.getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

	/**
	 * <pre>
	 *  관리자의 존재 여부를 확인하는 함수
	 * </pre>
	 * @author sunghkim
	 * @since 1.0
	 * @param name 확인할 관리자 ID
	 * @return 관리자가 존재한다면 true, 없다면 false를 반환
	 */
	public boolean findByAdminName(String name) {
		try {
			em.createQuery("select a from Administrator a where a.name = :name", Administrator.class)
					.setParameter("name", name)
					.getSingleResult();
			return true;
		} catch (NoResultException e) {
			return false;
		}
	}
}


