package openproject.where42.group;

import lombok.RequiredArgsConstructor;
import openproject.where42.group.domain.Groups;
import openproject.where42.member.MemberRepository;
import openproject.where42.member.domain.Member;
import openproject.where42.response.ResponseDto;
import openproject.where42.response.ResponseMsg;
import openproject.where42.response.StatusCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class GroupApiController {
    private final GroupService groupService;
    private final MemberRepository memberRepository;

    @PostMapping("/v1/member/{memberId}/group")
    public ResponseEntity createGroup(@PathVariable("memberId") Long memberId, @RequestParam("groupName") String groupName) {
        Long groupId = groupService.saveGroup(groupName, memberId); // false 예외처리??
        return new ResponseEntity(ResponseDto.res(StatusCode.OK, ResponseMsg.CREATE_GROUP, groupId), HttpStatus.OK);
    }

    @GetMapping("/v1/member/{memberId}/groups")
    public List<GroupInfoDto> getGroups(@PathVariable("memberId") Long memberId) {
        List<GroupInfoDto> result = new ArrayList<>();
        List<Groups> groups = groupService.findAllGroupsExceptDefault(memberId);
        Member member = memberRepository.findById(memberId);
        result.add(new GroupInfoDto(member.getStarredGroupId(), "즐겨찾기"));
        for (Groups g : groups)
            result.add(new GroupInfoDto(g.getId(), g.getGroupName()));
        return result;
    }

    @PostMapping("/v1/group/{groupId}")
    public ResponseEntity updateGroupName(@PathVariable("groupId") Long groupId, @RequestParam("changeName") String changeName) {
        groupService.updateGroupName(groupId, changeName);
        return new ResponseEntity(ResponseDto.res(StatusCode.OK, ResponseMsg.CHANGE_GROUP_NAME, groupId), HttpStatus.OK);
    }

    @DeleteMapping("/v1/group/{groupId}")
    public ResponseEntity deleteGroup(@PathVariable("groupId") Long groupId) {
        groupService.deleteByGroupId(groupId);
        return new ResponseEntity(ResponseDto.res(StatusCode.OK, ResponseMsg.DELETE_GROUP), HttpStatus.OK);
    }
}