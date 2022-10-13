package openproject.where42.groupFriend;

import lombok.RequiredArgsConstructor;
import openproject.where42.group.GroupService;
import openproject.where42.groupFriend.domain.GroupFriend;
import openproject.where42.groupFriend.dto.GroupFriendForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class GroupFriendController {
	public final GroupFriendService groupFriendService;
	public final GroupService groupService;

	@GetMapping("/groupFriend/new")
	public String createForm(Model model) {
		model.addAttribute("groupFriendForm", new GroupFriendForm());
		return "???"; // 그룹 추가할 때 새 페이지를 열어서 추가하나..?
	}

	@PostMapping("/groupFriend/new")
	public String create(@Valid GroupFriendForm form, BindingResult result) {
		if (result.hasErrors()) // 에러나면 메세지 출력하게끔 하는 부분인데 프론트가 어떻게 만드냐에 따라 다른듯
			return "???";
		groupFriendService.saveGroupFriend(form.getFriend_name(), form.getGroupId());
		return "redirect:/"; //영한씨가 홈에 보내라고 했다..
	}

	@GetMapping("/groupFriends/new")
	public String multicreateForm(Model model) {
		List<GroupFriend> list = null; // 이거 리스트 보내면 프론트에서 채워 넣을 수 있는건가...?
		model.addAttribute("groupFriendForms", list);
		return "???";
	}

	@PostMapping("/groupFriends/new")
	public String muticreate(@Valid List<GroupFriend> list) {
		groupFriendService.multiSaveGroupFriend(list);
		return "redirect:/";
	}

	@GetMapping("/groupFriend/{groupId}/delete")
	public String deleteGroupFriend(@PathVariable("groupId") Long groupId, Model model) {
//		List<GroupFriend> groupFriends = groupFriendService.groupFriendList(groupId); --> 그룹멤버 리스트를 줘야 화면에 뛰움
//		model.addAttribute("groupFriends", groupFriends);
		return "???";
	}

	@PostMapping("/groupFriend/{groupId}/delete")
	public String deleteGroupFriend(GroupFriend groupFriend) {
		groupFriendService.deleteGroupFriend(groupFriend);
		return "redirect:/";
	}

	@GetMapping("/groupFriends/{groupId}/delete")
	public String deleteGroupFriends(@PathVariable("groupId") Long groupId, Model model) {
//		List<GroupFriend> groupFriends = groupFriendService.groupFriendList(groupId); --> 그룹멤버 리스트를 줘야 화면에 뛰움
//		model.addAttribute("groupFriends", groupFriends);
		return "???";
	}

	@PostMapping("/groupFriends/{groupId}/delete")
	public String deleteGroupFriends(List<GroupFriend> groupFriends) {
		groupFriendService.deleteGroupFriends(groupFriends);
		return "redirect:/";
	}

}
