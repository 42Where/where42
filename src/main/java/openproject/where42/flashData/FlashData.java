package openproject.where42.flashData;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openproject.where42.util.Define;
import openproject.where42.member.entity.Locate;

import javax.persistence.*;

/**
 * <pre>
 *     플래시데이터 entity
 *     name: PK이며 image의 소유주 intra 아이디 [유니크]
 *     img: img URL
 *     inOrOut: 출입여부 확인
 *     locate: 위치 정보를 나타냄. @Embedded 를 사용하여 복합 속성 정의
 *     location: 파싱되기 전 위치정보를 나타냄 ex)c7r1s9
 * </pre>
 * @version 1.0
 * @author sunghkim
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class FlashData {
	@Id
	private String name;
	private String img;
	private int inOrOut;
	@Embedded
	private Locate locate = new Locate(null, 0, 0, null);
	private String location;

	/**
	 * 플래시 데이터 생성자
	 * @param name 카뎃 인트라 아이디
	 * @param img 이미지 주소
	 * @param location 아이맥 로그인 정보
	 * @since 1.0
	 * @author sunghkim
	 */
	public FlashData(String name, String img, String location) {
		this.name = name;
		this.img = img;
		this.location = location;
	}

	/**
	 * 아이맥 로그인 정보 업데이트
	 * @param location 아이맥 로그인 정보
	 * @since 1.0
	 * @author hyunjcho
	 */
	public void updateLocation(String location) {
		this.location = location;
	}

	/**
	 * locate 정보를 통해 inOrOut 상태 및 location parsed 상태로 갱신
	 * @param locate location 정보를 통해 파싱된 locate 정보
	 * @since 1.0
	 * @author hyunjcho
	 */
	public void parseStatus(Locate locate) {
		this.locate = locate;
		if (locate.getPlanet() != null)
			this.inOrOut = Define.IN;
		else
			this.inOrOut = Define.NONE;
		this.location = Define.PARSED;
	}
}
