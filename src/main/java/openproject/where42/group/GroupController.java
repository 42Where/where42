package openproject.where42.group;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;

    @PostMapping("/v1/member/group/{memberId}")
    public boolean createGroup(@PathVariable("memberId") Long memberId, @RequestParam("groupName") String groupName) {
        groupService.saveGroup(groupName, memberId); // false 예외처리??
        return true; // http 상태 반환?
    }

    @PostMapping("/v1/group/{groupId}")
    public boolean updateGroupName(@PathVariable("groupId") Long groupId, @RequestParam("changeName") String changeName) {
        return groupService.updateGroupName(groupId, changeName);
    }

    @DeleteMapping("/v1/group/{groupId}")
    public boolean deleteGroup(@PathVariable("groupId") Long groupId) {
        groupService.deleteByGroupId(groupId);
        return true;
    }
}