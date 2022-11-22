package openproject.where42.token.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Token {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "token_id")
	private Long id;

	@JoinColumn(name = "intra_id")
	private String memberName;
	@JoinColumn(name = "UUID")
	private String UUID;
	@JoinColumn(name = "refresh_token")
	private String refreshToken;

	@JoinColumn(name = "access_token")
	private String accessToken;
	private String recentLogin;

	public Token(String name, String access ,String refresh) {

		this.UUID = java.util.UUID.randomUUID().toString();
		this.accessToken = access;
		this.refreshToken = refresh;
		this.memberName = name;
		/*** update 시간 기록***/
		Long systemTime = System.currentTimeMillis();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss:Z'Z'",Locale.KOREA);
		String time = format.format(systemTime);
		this.recentLogin = time;
	}

	public void updateRefresh(String value) {
		this.refreshToken = value;
	}

	public String updateAccess(String value) {
		this.accessToken = value;
		return this.UUID;
	}
}
