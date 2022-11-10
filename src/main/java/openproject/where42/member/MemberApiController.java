package openproject.where42.member;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import openproject.where42.api.ApiService;
import openproject.where42.api.Define;
import openproject.where42.api.dto.Seoul42;
import openproject.where42.exception.TakenSeatException;
import openproject.where42.group.GroupRepository;
import openproject.where42.group.GroupService;
import openproject.where42.group.domain.Groups;
import openproject.where42.groupFriend.dto.GroupFriendInfoDto;
import openproject.where42.member.domain.Locate;
import openproject.where42.groupFriend.GroupFriendRepository;
import openproject.where42.member.domain.Member;
import openproject.where42.member.dto.MemberAll;
import openproject.where42.member.dto.MemberGroupInfo;
import openproject.where42.member.dto.MemberInfo;
import openproject.where42.response.ResponseDto;
import openproject.where42.response.ResponseMsg;
import openproject.where42.response.StatusCode;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final GroupService groupService;

    private final GroupRepository groupRepository;
    private final GroupFriendRepository groupFriendRepository;

    @PostMapping("/v1/member") // 동의 완료 시 넘어오는 주소 로그인 완료하고 dto 반환 -> 다시 이름 받기
    public ResponseEntity createMember(@RequestBody Seoul42 seoul42) {
        Long memberId = memberService.saveMember(seoul42.getLogin(), seoul42.getImage_url(), seoul42.getLocation());

        return new ResponseEntity(ResponseDto.res(StatusCode.OK, ResponseMsg.CREATE_MEMBER, memberId), HttpStatus.OK); // 첫 로그인 시에는 멤버 정보만 넘겨준다. 어차피 친구가 없으니까.
    }

    @GetMapping("/v1/member/{memberId}") // 두번째로그인 시 친구 목록 까지 쭉 반환되는거
    public ResponseMemberInfo memberInformation(@PathVariable ("memberId") Long memberId) { // 두번째 로그인 부터는 이거
        Member member = memberRepository.findById(memberId);
        MemberInfo memberInfo = new MemberInfo(member);
        if ((memberInfo.getInOutState() == Define.IN && memberInfo.getLocate() != null) || memberInfo.getInOutState() == Define.OUT)
            memberService.initializeLocate(member);
        System.out.println(memberInfo);
        List<MemberGroupInfo> groupList = memberService.findAllGroupFriendsInfo(member); // 그룹별 친구 오름차순 된거
        List<GroupFriendInfoDto> groupFriendsList = memberService.findAllFriendsInfo(member); // 해당 오너의 기본 그룹에 속한 친구들 정보 DTO로
        return new ResponseMemberInfo(memberInfo, groupList, groupFriendsList);
    }

    @Getter
    private static class ResponseMemberInfo {
        MemberInfo memberInfo;
        List<MemberGroupInfo> groupInfo;
        List<GroupFriendInfoDto> groupFriendsList;

        public ResponseMemberInfo(MemberInfo memberInfo, List<MemberGroupInfo> groupInfo, List<GroupFriendInfoDto> groupFriendsList) {
            this.memberInfo = memberInfo;
            this.groupInfo = groupInfo;
            this.groupFriendsList = groupFriendsList;
        }
    }

    @GetMapping("/v1/member/{memberId}/setting/msg") // 굳이 필요없나 싶기도 함
    public ResponseEntity getPersonalMsg(@PathVariable("memberId") Long memberId) {
        Member member = memberRepository.findById(memberId);
        return new ResponseEntity(ResponseDto.res(StatusCode.OK, ResponseMsg.GET_MSG, member.getMsg()), HttpStatus.OK);
    }
    @PostMapping("/v1/member/{memberId}/setting/msg")
    public ResponseEntity updatePersonalMsg(@PathVariable("memberId") Long memberId, @RequestBody String msg) {
        memberService.updatePersonalMsg(memberId, msg);
        return new ResponseEntity(ResponseDto.res(StatusCode.OK, ResponseMsg.SET_MSG), HttpStatus.OK);
    }

    @GetMapping("/v1/member/{memberId}/setting/locate")
    public ResponseEntity checkLocate(@PathVariable("memberId") Long memberId, @RequestParam String token) {
        ApiService apiService = new ApiService();
        ObjectMapper objectMapper = new ObjectMapper();
        if (3 == 3) {// hane 출근 - 자동정보 있는지 확인 해서 있으면 익셉션 보내주기
            HttpEntity<MultiValueMap<String, String>> req = apiService.req42ApiHeader(token);
            ResponseEntity<String> res = apiService.resApi(req, apiService.req42ApiOneUserUri(memberRepository.findById(memberId).getName()));
            Seoul42 seoul42 = null;
            try {
                seoul42 = objectMapper.readValue(res.getBody(), Seoul42.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            if (seoul42.getLocation() != null)
                throw new TakenSeatException();
        }
        return new ResponseEntity(ResponseDto.res(StatusCode.OK, ResponseMsg.NOT_TAKEN_SEAT), HttpStatus.OK);
    }

    @PostMapping("/v1/member/{memberId}/setting/locate")
    public ResponseEntity updateLocate(@PathVariable("memberId") Long memberId, @RequestBody Locate locate) { // token이 필요하다.. 어디서.. 누가 줘야 할까.. ㅠ 우리 쿠키로 주면 젤 좋음 ㅠ
        Member member = memberRepository.findById(memberId);
        memberService.updateLocate(member, locate);
        return new ResponseEntity(ResponseDto.res(StatusCode.OK, ResponseMsg.SET_LOCATE), HttpStatus.OK);
    }

    @GetMapping("/v1/member/{memberId}/all") // 삭제할 메소드
    public MemberAll memberAll(@PathVariable("memberId") Long memberId) {
        Member member = memberRepository.findById(memberId);
        return new MemberAll(member.getName(), member.getMsg(), member.getLocate(), groupService.findAllGroupsExceptDefault(memberId), groupFriendRepository.findGroupFriendsByGroupId(member.getDefaultGroupId()));
    }

    @GetMapping("/v1/member/{id}/allGroup") // 삭제할 메소드
    public List<Groups> groupList(@PathVariable("id") Long id) {
        List<Groups> groups = groupRepository.findGroupsByOwnerId(id);
        return groups;
    }
}