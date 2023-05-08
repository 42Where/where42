package openproject.where42.token;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <pre>
 *     그룹 entity
 *     id: PK로 테이블의 고유값 [유니크]
 *     memberName: 널값이 불가능하며 멤버의 이름을 고유한 값으로 갖는다 [유니크]
 *     UUID: token을 조회할 때 사용하는 key값. 고유한 UUID 값을 생성해 저장
 *     refreshToken: refresh 토큰을 저장
 *     accessToken: access 토큰을 저장
 *     recentLogin: access 토큰을 발급받은 시각을 저장
 * </pre>
 * 그룹 entity
 * @see openproject.where42.group
 * @versin 1.0
 * @author sunghkim
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Token {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "member_name", nullable = false, unique = true)
	private String memberName;
	private String UUID;
	private String refreshToken;
	private String accessToken;
	@Temporal(TemporalType.TIMESTAMP)
	private Date recentLogin;

	public Token(String name, String access ,String refresh) {

		this.UUID = java.util.UUID.randomUUID().toString();
		this.accessToken = access;
		this.refreshToken = refresh;
		this.memberName = name;
		/*** update 시간 기록***/
		this.recentLogin = new Date();
	}

	public void updateRefresh(String value) {
		this.refreshToken = value;
	}

	public String updateAccess(String value) {
		this.accessToken = value;
		this.recentLogin = new Date();
		return this.UUID;
	}
}
