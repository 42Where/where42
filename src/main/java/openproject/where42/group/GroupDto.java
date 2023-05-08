package openproject.where42.group;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * <pre>
 *     그룹 관련 api 반환용 DTO 클래스
 *     groupId: 그룹 id
 *     groupName: 그룹 이름
 * </pre>
 * @see openproject.where42.group
 * @version 1.0
 * @author hyunjcho
 */
@Getter @Setter
@AllArgsConstructor
public class GroupDto {
    Long groupId;
    String groupName;
}