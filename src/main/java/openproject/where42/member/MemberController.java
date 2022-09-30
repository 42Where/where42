package openproject.where42.member;

import lombok.RequiredArgsConstructor;
import openproject.where42.member.dto.LocateForm;
import openproject.where42.member.dto.MemberForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/profile/setting")
    public String createMsgForm(Model model) {
        model.addAttribute("form", new MemberForm());
        return "profile/updateMsg";
    }

    @PostMapping("/groups/setting")
    public String updatePersonalMsg(MemberForm form) {
        memberService.updatePersonalMsg(form.getMemberId(), form.getMsg());
        return "redirect:/profile";
    }

    @GetMapping("/profile/setting")
    public String createLocateForm(Model model) {
        model.addAttribute("form", new LocateForm());
        return "profile/updateLocate";
    }

    @PostMapping("/groups/setting")
    public String updateLocate(LocateForm form) {
        memberService.updateLocate(form.getMemberId(), form.getCluster(), form.getFloor(), form.getLocate());
        return "redirect:/profile";
    }

    // 요청자에 대한 정보 반환해주는 컨트롤러 만들어야 함 (h)
}
