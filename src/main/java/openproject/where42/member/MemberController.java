package openproject.where42.member;

import lombok.RequiredArgsConstructor;
import openproject.where42.group.GroupService;
import openproject.where42.group.domain.Groups;
import openproject.where42.group.GroupRepository;
import openproject.where42.member.domain.Locate;
import openproject.where42.response.ResponseDto;
import openproject.where42.response.ResponseMsg;
import openproject.where42.response.StatusCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final GroupService groupService;
    private final GroupRepository groupRepository;

    @PostMapping("/v1/member/{name}") // 동의 완료 시 넘어오는 주소 로그인 완료하고 dto 반환 -> 다시 이름 받기
    public ResponseEntity createMember(@PathVariable("name") String name) {
        Long memberId = memberService.saveMember(name);
        return new ResponseEntity(ResponseDto.res(StatusCode.OK, ResponseMsg.CREATE_MEMBER, memberId), HttpStatus.OK);
        // memberid 반환해줘야하나? 프론트에 확인 할 것 뭐 가지고 있어야 하는지. + 로케이트도 줬다가 다시 받아서 다시 주자 me다시 안부르게
    }

    @PostMapping("/v1/member/{memberId}/profile/msg")
    public ResponseEntity updatePersonalMsg(@PathVariable ("memberId") Long memberId, @RequestBody String msg) { //param? body??
        memberService.updatePersonalMsg(memberId, msg);
        return new ResponseEntity(ResponseDto.res(StatusCode.OK, ResponseMsg.SET_MSG), HttpStatus.OK);
    }

    @PostMapping("/v1/member/{memberId}/profile/locate")
    public ResponseEntity updateLocate(@PathVariable("memberId") Long memberId, @RequestBody Locate locate) {
        memberService.updateLocate(memberId, locate); // 우리가 자동정보 있는지를 확인해 줘야 하는지?, locate를 어떻게 받는 것인지?
        return new ResponseEntity(ResponseDto.res(StatusCode.OK, ResponseMsg.SET_LOCATE), HttpStatus.OK);
    }

    @GetMapping("/member/{id}/allGroup") // 삭제할 메소드
    public String groupList(@PathVariable("id") Long id, Model model) {
        List<Groups> groups = groupRepository.findGroupsByOwnerId(id);
        System.out.println("groups = ");
        for (Groups g: groups)
            System.out.println(g.getGroupName());
        model.addAttribute("groups", groups);
        model.addAttribute("memberId", id);
        return "member/groupList";
    }
}
