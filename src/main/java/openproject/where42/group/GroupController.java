package openproject.where42.group;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;

    @GetMapping("/member/{memberId}/group")
    String createGroupForm(@PathVariable("memberId") Long memberId, Model model) {
        String name = "name";

        model.addAttribute("name", name);
        return "member/createGroupFrom"; // form 필요 없으면 삭제해도 되는 컨트롤러 걍 리퀘스트바디로 받기
    }

    @PostMapping("/member/{memberId}/group")
    String createGroup(@PathVariable("memberId") Long memberId, @ModelAttribute("name") String name) {
        groupService.saveGroup(name, memberId);
        return "member/iAm";
    }

//    @PostMapping("/member/{memberId}/group")
//    public String createGroup(@PathVariable("memberId") Long memberId, @RequestBody String groupName) {
//        groupService.saveGroup(groupName, memberId);
//        return "member/iAm";
//    }

    @GetMapping("/member/group/{groupsId}")
    public String updateGroupForm(@PathVariable("groupsId") Long groupsId, Model model) {
        String name = "name";

        model.addAttribute("name", name);
        return "member/updateGroupForm"; // form 줄 필요 없으면 삭제해도 되는 컨트롤러
    }

    @PostMapping("/member/group/{groupsId}")
    public String updateGroupName(@PathVariable("groupsId") Long groupsId, @ModelAttribute("name") String name) {
        groupService.updateGroupName(groupsId, name);
        return "member/iAm"; // post 방식에 따라 바뀔 컨트롤러
    }
//    @PostMapping("/member/group/{groupsId}")
//    public String updateGroupName(@PathVariable("groupsId") Long groupsId, String updateGroupName) {
//        groupService.updateGroupName(groupsId, updateGroupName);
//        return "/member/iAm";
//    } // post 방식으로 string 바로 받아올 수 있으면 이 컨트롤러 살리면 됨

    @DeleteMapping("/member/{memberId}/{groupId}")
    public String deleteGroup(@PathVariable("groupId") Long groupId) {
        groupService.deleteByGroupId(groupId);
        return "member/iAm";
    }
}