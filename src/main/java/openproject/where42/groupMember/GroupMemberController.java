package openproject.where42.groupMember;

import lombok.RequiredArgsConstructor;
import openproject.where42.groupMember.dto.GroupMemberForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class GroupMemberController {
	public final GroupMemberService groupMemberService;

	@GetMapping("/groupMember/new")
	public String createForm(Model model) {
		model.addAttribute("groupMemberForm", new GroupMemberForm());
		return "???"; // 그룹 추가할 때 새 페이지를 열어서 추가하나..?
	}

	@PostMapping("/groupMember/new")
	public String create(@Valid GroupMemberForm form, BindingResult result) {
		if (result.hasErrors()) // 에러나면 메세지 출력하게끔 하는 부분인데 프론트가 어떻게 만드냐에 따라 다른듯
			return "???";
		groupMemberService.saveGroupMember(form.getFriend_name(), form.getGroupId());
		return "redirect:/"; //영한씨가 홈에 보내라고 했다..
	}
}
