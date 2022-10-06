package openproject.where42.groupMember;

import lombok.RequiredArgsConstructor;
import openproject.where42.group.GroupService;
import openproject.where42.group.domain.Groups;
import openproject.where42.groupMember.domain.GroupMember;
import openproject.where42.groupMember.dto.GroupMemberForm;
import openproject.where42.member.MemberService;
import openproject.where42.member.repository.MemberRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class GroupMemberController {
	public final GroupMemberService groupMemberService;
	public final GroupService groupService;

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

	@GetMapping("/groupMembers/new")
	public String multicreateForm(Model model) {
		List<GroupMember> list = null; // 이거 리스트 보내면 프론트에서 채워 넣을 수 있는건가...?
		model.addAttribute("groupMemberForms", list);
		return "???";
	}

	@PostMapping("/groupMembers/new")
	public String muticreate(@Valid List<GroupMember> list) {
		groupMemberService.multiSaveGroupMember(list);
		return "redirect:/";
	}

	@GetMapping("/groupMember/{groupId}/delete")
	public String deleteGroupMember(@PathVariable("groupId") Long groupId, Model model) {
//		List<GroupMember> groupMembers = groupMemberService.groupMemberList(groupId); --> 그룹멤버 리스트를 줘야 화면에 뛰움
//		model.addAttribute("groupMembers", groupMembers);
		return "???";
	}

	@PostMapping("/groupMember/{groupId}/delete")
	public String deleteGroupMember(GroupMember groupMember) {
		groupMemberService.deleteGroupMember(groupMember);
		return "redirect:/";
	}

	@GetMapping("/groupMembers/{groupId}/delete")
	public String deleteGroupMembers(@PathVariable("groupId") Long groupId, Model model) {
//		List<GroupMember> groupMembers = groupMemberService.groupMemberList(groupId); --> 그룹멤버 리스트를 줘야 화면에 뛰움
//		model.addAttribute("groupMembers", groupMembers);
		return "???";
	}

	@PostMapping("/groupMembers/{groupId}/delete")
	public String deleteGroupMembers(List<GroupMember> groupMembers) {
		groupMemberService.deleteGroupMembers(groupMembers);
		return "redirect:/";
	}

}
