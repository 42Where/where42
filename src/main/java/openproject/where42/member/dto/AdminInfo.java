package openproject.where42.member.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * <pre>
 *     관리자 관련 api 반환용 DTO 클래스
 *     name: 관리자 id
 *     passwd: 비밀번호
 *     얘도 어드민 클래스로 옮겨야 함
 * </pre>
 * @see openproject.where42.group
 * @version 1.0
 * @author hyunjcho
 */
@Getter @Setter
public class AdminInfo {
	private String name;
	private String passwd;
}
