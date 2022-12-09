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
		return this.UUID;
	}
}
