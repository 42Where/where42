package openproject.where42.api.mapper;

import lombok.Getter;

/**
 * <pre>
 *     하네 정보 매핑용 클래스
 *     login: 정보 조회할 카뎃 이름
 *     inoutState: 출-퇴근 정보
 *     cluster: 출근했을 경우 출근한 클러스터 정보
 *     tag_at: 출-퇴근 카드를 태깅한 시각
 * </pre>
 * @version 1.0
 * @author hyunjcho
 */
@Getter
public class Hane {
    private String login;
    private String inoutState;
    private String cluster;
    private String tag_at;
}
