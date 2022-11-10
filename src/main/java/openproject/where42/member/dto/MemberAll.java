package openproject.where42.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import openproject.where42.group.domain.Groups;
import openproject.where42.member.domain.Locate;

import java.util.List;

@AllArgsConstructor
@Getter @Setter
public class MemberAll { // 나중에 없앨 클래스
    private String name;
    private String msg;
    private Locate locate;
    private List<Groups> groupList;
    private List<String> groupFriendList;
}
