package openproject.where42.api.mapper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

/**
 * <pre>
 *     클러스터 아이맥 로그인-아웃 정보 매핑용 클래스
 *     end_at: 해당 아이맥 로그아웃 시각
 *     begin_at: 해당 아이맥 로그인 시각
 *     user: 아이맥에 로그인-아웃 한 카뎃 정보
 * </pre>
 * @see User
 * @version 1.0
 * @author hyunjcho
 */
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Cluster {
    String end_at;
    String begin_at;
    User user;
}