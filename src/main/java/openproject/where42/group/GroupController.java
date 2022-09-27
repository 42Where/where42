package openproject.where42.group;

import lombok.RequiredArgsConstructor;
import openproject.where42.group.domain.Groups;
import openproject.where42.member.MemberService;
import openproject.where42.member.domain.Member;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;
    private final MemberService memberService;

    @GetMapping("/groups/new")
    public String createForm(Model model) {
        model.addAttribute("form", new GroupForm());
        return "groups/createGroupForm";
    }

    @PostMapping("/groups/new")
    public String createGroup(GroupForm form) {
        Member owner = memberService.findOne(form.getMemberName());
        Groups group = new Groups(form.getGroupName(), owner);
        groupService.saveGroup(group);
        return "redirect:/";
    }
}