package openproject.where42.member;

import lombok.RequiredArgsConstructor;
import openproject.where42.group.GroupService;
import openproject.where42.group.domain.Groups;
import openproject.where42.group.repository.GroupRepository;
import openproject.where42.member.domain.Member;
import openproject.where42.member.domain.enums.MemberLevel;
import openproject.where42.member.dto.LocateForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final GroupService groupService;

    private final GroupRepository groupRepository;

    @PostMapping("/member/{memberId}/profile/msg")
    public String updatePersonalMsg(@PathVariable ("memberId") Long memberId, @RequestBody String msg) {
        memberService.updatePersonalMsg(memberId, msg);
        return "/member/{memberId}/profile";
    }

    @GetMapping("/member/{memberId}/profile/locate") // front form 객체 필요한지?, {} 매핑주소 뺴도 되는지..
    public String createLocateForm(Model model) {
        model.addAttribute("form", new LocateForm()); // locateform말고 걍 Locate 갖다 써도 되는지?
        return "/member/{memberId}/profile/updateLocate";
    }

    @PostMapping("/member/{memberId}/profile/locate")
    public String updateLocate(@PathVariable("memberId") Long memberId, @RequestBody LocateForm form) {
        memberService.updateLocate(memberId, form);
        return "/member/{memberId}/profile";
    }

    @PostMapping("/member/{name}")
    public String createMember(@PathVariable("name") String name, Model model) {
        Member member = new Member(name, MemberLevel.member);
        Long defaultGroupId = groupService.createDefaultGroup(member, "기본");
        Long starredGroupId = groupService.createDefaultGroup(member, "즐겨찾기");
        System.out.println("defaultGroupId = " + defaultGroupId + " starred = " + starredGroupId);
        member.setDefaultGroup(defaultGroupId, starredGroupId);
        memberService.createMember(member);
        model.addAttribute(member); // member dto ? id ?
        return "member/iAm"; // 해당 멤버 메인화면으로 반환되어야 함
    }

    @GetMapping("/member/{id}/groups")
    public String groupList(@PathVariable("id") Long id, Model model) {
        List<Groups> groups = groupRepository.findGroupsByOwnerId(id);
        model.addAttribute("groups", groups);
        return "member/groupList";
    }
}
