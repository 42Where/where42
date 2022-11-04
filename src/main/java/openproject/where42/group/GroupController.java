package openproject.where42.group;

import lombok.RequiredArgsConstructor;
import openproject.where42.response.ResponseDto;
import openproject.where42.response.ResponseMsg;
import openproject.where42.response.StatusCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;

    @PostMapping("/v1/member/group/{memberId}")
    public ResponseEntity createGroup(@PathVariable("memberId") Long memberId, @RequestParam("groupName") String groupName) {
        groupService.saveGroup(groupName, memberId); // false 예외처리??
        return new ResponseEntity(ResponseDto.res(StatusCode.OK, ResponseMsg.CREATE_GROUP), HttpStatus.OK);
    }

    @PostMapping("/v1/group/{groupId}")
    public ResponseEntity updateGroupName(@PathVariable("groupId") Long groupId, @RequestParam("changeName") String changeName) {
        groupService.updateGroupName(groupId, changeName);
        return new ResponseEntity(ResponseDto.res(StatusCode.OK, ResponseMsg.CHANGE_GROUP_NAME), HttpStatus.OK);
    }

    @DeleteMapping("/v1/group/{groupId}")
    public ResponseEntity deleteGroup(@PathVariable("groupId") Long groupId) {
        groupService.deleteByGroupId(groupId);
        return new ResponseEntity(ResponseDto.res(StatusCode.OK, ResponseMsg.DELETE_GROUP), HttpStatus.OK);
    }
}