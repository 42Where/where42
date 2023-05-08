package openproject.where42.group;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openproject.where42.util.Define;
import openproject.where42.member.entity.Member;
import openproject.where42.util.response.Response;
import openproject.where42.util.response.ResponseWithData;
import openproject.where42.util.response.ResponseMsg;
import openproject.where42.util.response.StatusCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 그룹 관련 컨트롤러
 * @version 1.0
 * @see openproject.where42.group
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class GroupApiController {
    private final GroupService groupService;

    /**
     * 멤버의 커스텀 그룹 생성 api
     * @param req 멤버 세션 확인용 HttpServletRequest
     * @param res 멤버 토큰 확인용 HttpServletResponse
     * @param key 멤버 토큰 확인용 쿠키값
     * @param groupName 생성할 그룹 이름
     * @return 그룹 생성 성공에 대한 status code와 생성된 친구 아이디 반환
     * @throws openproject.where42.exception.customException.UnregisteredMemberException 로그인 정보를 확인 할 수 없는 유저일 경우 401 예외 throw
     * @throws openproject.where42.exception.customException.TokenExpiredException 토큰 쿠키를 찾을 수 없는 경우 401 예외 throw
     * @throws openproject.where42.exception.customException.DuplicateGroupNameException 이미 존재하는 그룹 이름일 경우 409 예외 throw
     * @see openproject.where42.group.GroupService#findOwnerBySessionWithToken(HttpServletRequest, HttpServletResponse, String) 세션 및 토큰으로 오너 찾기
     * @see openproject.where42.group.GroupService#createCustomGroup(String, Member) 그룹 생성
     * @since 1.0
     * @author hyunjcho
     */
    @PostMapping(Define.WHERE42_VERSION_PATH + "/group")
    public ResponseEntity createCustomGroup(HttpServletRequest req, HttpServletResponse res, @CookieValue(value = "ID", required = false) String key, @RequestParam("groupName") String groupName) {
        Member owner = groupService.findOwnerBySessionWithToken(req, res, key);
        Long groupId = groupService.createCustomGroup(groupName, owner);
        log.info("[group] '{}'님이 그룹을 생성하였습니다.", owner.getName());
        return new ResponseEntity(ResponseWithData.res(StatusCode.CREATED, ResponseMsg.CREATE_GROUP, groupId), HttpStatus.CREATED);
    }

    /**
     * 멤버의 기본 그룹을 제외한 전체 그룹 목록 조회(즐겨찾기 그룹 포함) api
     * @param req 멤버 세션 확인용 HttpServletRequest
     * @param res 멤버 토큰 확인용 HttpServletResponse
     * @param key 멤버 토큰 확인용 쿠키값
     * @return 전체 그룹목록 DTO 리스트 반환
     * @throws openproject.where42.exception.customException.UnregisteredMemberException
     * @throws openproject.where42.exception.customException.TokenExpiredException 토큰 쿠키를 찾을 수 없는 경우 401 예외 throw
     * @see openproject.where42.group.GroupService#findOwnerBySessionWithToken(HttpServletRequest, HttpServletResponse, String) 세션 및 토큰으로 오너 찾기
     * @see openproject.where42.group.GroupService#findAllGroupsExceptDefault(Long) 기본 그룹을 제외한 모든 그룹 조회
     * @since 1.0
     * @author hyunjcho
     *
     */
    @GetMapping(Define.WHERE42_VERSION_PATH + "/group")
    public List<GroupDto> getGroupsExceptDefault(HttpServletRequest req, HttpServletResponse res, @CookieValue(value = "ID", required = false) String key) {
        Member member = groupService.findOwnerBySessionWithToken(req, res, key);
        List<Groups> groups = groupService.findAllGroupsExceptDefault(member.getId());
        List<GroupDto> result = new ArrayList<>();
        result.add(new GroupDto(member.getStarredGroupId(), "즐겨찾기"));
        for (Groups g : groups)
            result.add(new GroupDto(g.getId(), g.getGroupName()));
        return result;
    }

    /**
     * <pre>
     *     기본 및 즐겨찾기를 제외한 인자로 받은 그룹의 이름 수정 api
     *     groupId 유니크성으로 요청 멤버에 대한 유효성 검사는 진행하지 않음
     * </pre>
     * @param groupId 이름을 수정할 그룹 아이디
     * @param changeName 변경할 그룹 이름
     * @return 이름 변경 성공 status code와 groupId 반환
     * @throws openproject.where42.exception.customException.BadRequestException 존재하지 않는 그룹에 대해 요청할 경우 400 예외 throw
     * @throws openproject.where42.exception.customException.DuplicateGroupNameException 이미 존재하는 그룹 이름일 경우 409 예외 throw
     * @see openproject.where42.group.GroupService#updateGroupName(Long, String) 그룹 이름 갱신
     * @since 1.0
     * @author hyunjcho
     */
    @PostMapping(Define.WHERE42_VERSION_PATH + "/group/{groupId}")
    public ResponseEntity updateGroupName(@PathVariable("groupId") Long groupId, @RequestParam("changeName") String changeName) {
        groupService.updateGroupName(groupId, changeName);
        return new ResponseEntity(ResponseWithData.res(StatusCode.OK, ResponseMsg.CHANGE_GROUP_NAME, groupId), HttpStatus.OK);
    }

    /**
     * <pre>
     *     인자로 받은 그룹 삭제
     *     해당 그룹에 포함되어 있던 친구들도 자동으로 삭제 됨
     *     groupId 유니크성으로 요청 멤버에 대한 유효성 검사는 진행하지 않음
     * </pre>
     * @param groupId 삭제할 그룹 아이디
     * @return 삭제 성공 status code 반환
     * @throws openproject.where42.exception.customException.BadRequestException 존재하지 않는 그룹에 대해 요청할 경우 400 예외 throw
     * @see openproject.where42.group.GroupService#deleteByGroupId(Long) 그룹 아이디로 그룹 삭제
     * @since 1.0
     * @author hyunjcho
     */
    @DeleteMapping(Define.WHERE42_VERSION_PATH + "/group/{groupId}")
    public ResponseEntity deleteGroup(@PathVariable("groupId") Long groupId) {
        groupService.deleteByGroupId(groupId);
        return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.DELETE_GROUP), HttpStatus.OK);
    }
}