package openproject.where42.api.mapper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

/**
 * <pre>
 *     42api 카뎃 정보 매핑용 클래스
 *     cluster 아이맥 로그인-아웃 정보 조회 시 해당 유저 정보는 User에 매핑됨
 *     login: 카뎃 닉네임
 *     Image: 이미지 전체 정보를 가진 클래스
 *     location: 현재 해당 카뎃의 location 정보, 아이맥에 로그인하지 않았을 경우 null
 * </pre>
 * @see Cluster
 * @see Image
 * @version 1.0
 * @author hyunjcho
 */
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    String login;
    Image image;
    String location;
}
