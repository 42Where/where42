package openproject.where42.background;

import lombok.Getter;

import javax.persistence.*;

/**
 * <pre>
 *     id: PK이며 고유 식별자 [유니크]
 *     name: image의 소유주 intra 아이디
 *     img: img URL
 *     location: 위치 정보
 *     active: ..? 뭐에 쓰더라..
 * </pre>
 * @version 1.0
 * @author sunghkim
 */
@Entity
@Getter
@Table(name = "image")
public class Image {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	String name;
	String img;
	String location;
	boolean active;
}
