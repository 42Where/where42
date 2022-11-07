package openproject.where42.groupFriend;

import lombok.RequiredArgsConstructor;
import openproject.where42.group.GroupService;
import openproject.where42.groupFriend.domain.GroupFriend;
import openproject.where42.groupFriend.domain.GroupFriendInfo;
import openproject.where42.groupFriend.dto.FriendForm;
import openproject.where42.groupFriend.dto.GroupFriendForm;
import openproject.where42.response.ResponseDto;
import openproject.where42.response.ResponseMsg;
import openproject.where42.response.StatusCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class GroupFriendController {
	public final GroupFriendService groupFriendService;
	public final GroupService groupService;

	@PostMapping("/groupFriend/new")
	public ResponseEntity create(@Valid GroupFriendForm form, BindingResult result) {
		if (result.hasErrors())
			return new ResponseEntity(ResponseDto.res(StatusCode.BAD_REQUEST,ResponseMsg.NOT_FOUND_USER), HttpStatus.BAD_REQUEST);
		groupFriendService.saveGroupFriend(form.getFriend_name(), form.getGroupId());
		return new ResponseEntity(ResponseDto.res(StatusCode.OK, ResponseMsg.CREATE_GROUP_FRIEND), HttpStatus.OK);
	}

	@PostMapping("/groupFriends/new")
	public ResponseEntity muticreate(@Valid List<GroupFriend> list) {
		groupFriendService.multiSaveGroupFriend(list);
		return new ResponseEntity(ResponseDto.res(StatusCode.OK, ResponseMsg.CREATE_GROUP_FRIEND), HttpStatus.OK);
	}

	@PostMapping("/groupFriend/{groupId}/delete")
	public ResponseEntity deleteGroupFriend(GroupFriend groupFriend) {
		groupFriendService.deleteGroupFriend(groupFriend);
		return new ResponseEntity(ResponseDto.res(StatusCode.OK, ResponseMsg.DELETE_GROUP_FRIEND), HttpStatus.OK);
	}

//	@GetMapping("/groupFriends/{groupId}/delete")
//	public String deleteGroupFriends(@PathVariable("groupId") Long groupId, Model model) {
//		List<FriendForm> groupFriends = groupFriendService.findAllFriendsInfo(groupId);
//		model.addAttribute("groupFriends", groupFriends);
//		return "???";
//	}

	@PostMapping("/groupFriends/{groupId}/delete")
	public ResponseEntity deleteGroupFriends(@PathVariable("groupId") Long groupId, List<GroupFriend> groupFriends) {
		return new ResponseEntity(ResponseDto.res(StatusCode.OK, ResponseMsg.DELETE_GROUP_FRIEND), HttpStatus.OK);
	}

}
