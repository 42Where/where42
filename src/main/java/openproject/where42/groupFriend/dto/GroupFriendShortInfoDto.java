package openproject.where42.groupFriend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GroupFriendShortInfoDto {
    private Long friendId;
    private String friendName;
}
