package openproject.where42.api.mapper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * <pre>
 *     42api 카뎃 정보 매핑용 클래스
 *     login: 카뎃 닉네임
 *     location: 현재 해당 카뎃의 location 정보, 아이맥에 로그인하지 않았을 경우 null
 *     Image: 이미지 전체 정보를 가진 클래스
 *     active: 블랙홀 조회
 * </pre>
 * @see Image
 * @version 1.0
 * @author hyunjcho
 */
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Seoul42 {
    private String login;
    private String location;
    private Image image;
    @JsonProperty("active?")
    private boolean active;
    private String created_at;
}