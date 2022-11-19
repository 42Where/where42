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
	@JoinColumn(name = "UUID")
	private String UUID;
	@JoinColumn(name = "token")
	private String token;

	private String recentLogin;

	public Token(String value) {

		this.UUID = java.util.UUID.randomUUID().toString();
		this.token = value;
		/*** update 시간 기록***/
		Long systemTime = System.currentTimeMillis();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss:Z'Z'",Locale.KOREA);
		String time = format.format(systemTime);
		this.recentLogin = time;
	}

	public void updateValue(String value) {
		this.token = value;
	}
}
