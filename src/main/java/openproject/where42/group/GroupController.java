package openproject.where42.group;

import lombok.RequiredArgsConstructor;
import openproject.where42.group.dto.GroupForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;

    @GetMapping("/groups/new")
    public String createForm(Model model) {
        model.addAttribute("form", new GroupForm());
        return "groups/createGroupForm";
    }

    @PostMapping("/groups/new")
    public String createGroup(GroupForm form) {
        groupService.saveGroup(form.getGroupName(), form.getMemberId());
        return "redirect:/";
    }
    @PostMapping("/groups/{groupId}/delete") // delete이런거 url로 하지 말랬는디.. 예외처리는 다 서비스에서 하는건가?
    public String deleteGroup(@PathVariable("groupId") Long groupId) {
        groupService.deleteGroup(groupId);
        return "redirect:/";
    }
}