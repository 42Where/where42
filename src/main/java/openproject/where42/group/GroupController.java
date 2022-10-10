package openproject.where42.group;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;

    @PostMapping("/member/{memberId}/group")
    public String createGroup(@PathVariable("memberId") Long memberId, @RequestBody String groupName) {
        groupService.saveGroup(groupName, memberId);
        return "/member/{memberId}";
    }

    @PostMapping("/member/{memberId}/{groupId}")
    public String updateGroupName(@PathVariable("groupId") Long groupId, String updateGroupName) {
        groupService.updateGroupName(groupId, updateGroupName);
        return "/member/{memberId}";
    }

    @DeleteMapping("/member/{memberId}/{groupId}") // 아니.. groupcontroller인데 왜 오류가 뜨는거야; 아 재배가 안바꾼건가 리포지터리에서
    public String deleteGroup(@PathVariable("groupId") Long groupId) {
        groupService.deleteByGroupId(groupId);
        return "/member/{memberId}";
    }
}