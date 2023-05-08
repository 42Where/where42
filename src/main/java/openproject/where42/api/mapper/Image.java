package openproject.where42.api.mapper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <pre>
 *     카뎃 이미지 정보 매핑용 클래스
 *     link: 카뎃 이미지 주소
 * </pre>
 * @see Seoul42
 * @see User
 * @version 1.0
 * @author hyunjcho
 */
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Image {
    private String link;
}
