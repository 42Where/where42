package openproject.where42.member;

import lombok.RequiredArgsConstructor;
import openproject.where42.member.dto.LocateForm;
import openproject.where42.member.dto.MemberForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/member/{memberId}/profile/msg")
    public String updatePersonalMsg(@PathVariable ("memberId") Long memberId, @RequestBody String msg) {
        memberService.updatePersonalMsg(memberId, msg);
        return "/member/{memberId}/profile";
    }

    @GetMapping("/member/{memberId}/profile/locate") // front form 객체 필요한지?, {} 매핑주소 뺴도 되는지..
    public String createLocateForm(Model model) {
        model.addAttribute("form", new LocateForm());
        return "/member/{memberId}/profile/updateLocate";
    }

    @PostMapping("/member/{memberId}/profile/locate")
    public String updateLocate(@PathVariable("memberId") Long memberId, @RequestBody LocateForm form) {
        memberService.updateLocate(memberId, form.getCluster(), form.getFloor(), form.getLocate());
        return "/member/{memberId}/profile";
    }
}
