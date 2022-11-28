package openproject.where42.group;

import lombok.RequiredArgsConstructor;
import openproject.where42.api.Define;
import openproject.where42.exception.customException.DuplicateGroupNameException;
import openproject.where42.exception.customException.SessionExpiredException;
import openproject.where42.group.entity.GroupDto;
import openproject.where42.group.entity.Groups;
import openproject.where42.member.entity.Member;
import openproject.where42.util.response.Response;
import openproject.where42.util.response.ResponseWithData;
import openproject.where42.util.response.ResponseMsg;
import openproject.where42.util.response.StatusCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class GroupApiController {
    private final GroupService groupService;

    // 커스텀 그룹 생성
    @PostMapping(Define.versionPath + "/group")
    public ResponseEntity createCustomGroup(HttpServletRequest req, @RequestParam("groupName") String groupName) throws SessionExpiredException, DuplicateGroupNameException {
        Member owner = groupService.findOwnerBySession(req);
        Long groupId = groupService.createCustomGroup(groupName, owner);
        return new ResponseEntity(ResponseWithData.res(StatusCode.CREATED, ResponseMsg.CREATE_GROUP, groupId), HttpStatus.CREATED);
    }

    // 기본 그룹 제외 그룹 목록 반환 (그룹 관리)
    @GetMapping(Define.versionPath + "/group")
    public List<GroupDto> getGroupsExceptDefault(HttpServletRequest req) throws SessionExpiredException {
        Member member = groupService.findOwnerBySession(req);
        List<Groups> groups = groupService.findAllGroupsExceptDefault(member.getId());
        List<GroupDto> result = new ArrayList<>();
        result.add(new GroupDto(member.getStarredGroupId(), "즐겨찾기"));
        for (Groups g : groups)
            result.add(new GroupDto(g.getId(), g.getGroupName()));
        return result;
    }

    // 커스텀 그룹 이름 수정 -> 세션 만료되어도 저장 됨
    @PostMapping(Define.versionPath + "/group/{groupId}")
    public ResponseEntity updateGroupName(@PathVariable("groupId") Long groupId, @RequestParam("changeName") String changeName) throws DuplicateGroupNameException {
        groupService.updateGroupName(groupId, changeName);
        return new ResponseEntity(ResponseWithData.res(StatusCode.OK, ResponseMsg.CHANGE_GROUP_NAME, groupId), HttpStatus.OK);
    }

    // 세션 만료되어도 저장 됨
    @DeleteMapping(Define.versionPath + "/group/{groupId}")
    public ResponseEntity deleteGroup(@PathVariable("groupId") Long groupId) {
        groupService.deleteByGroupId(groupId);
        return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.DELETE_GROUP), HttpStatus.OK);
    }
}