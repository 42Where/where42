package openproject.where42.group;

import lombok.RequiredArgsConstructor;
import openproject.where42.member.domain.Member;
import openproject.where42.member.repository.MemberRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;
    private final MemberRepository memberRepository;

    @GetMapping("/member/{memberId}/group")
    String createGroupForm(@PathVariable("memberId") Long memberId, Model model) {
        GroupForm groupForm = new GroupForm();

        model.addAttribute("groupForm", groupForm);
        return "member/createGroupForm"; // form 필요 없으면 삭제해도 되는 컨트롤러 걍 리퀘스트바디로 받기
    }

    @PostMapping("/member/{memberId}/group")
    String createGroup(@PathVariable("memberId") Long memberId, @ModelAttribute("name") GroupForm name, Model model) {
        groupService.saveGroup(name.getName(), memberId);
        Member member = memberRepository.findById(memberId);
        model.addAttribute("member", member); // dto 변경 필요
        System.out.println("그룹 기본 = " + member.getDefaultGroupId() + "즐겨찾기 = " + member.getStarredGroupId());
        return "member/iAm";
    }

//    @PostMapping("/member/{memberId}/group")
//    public String createGroup(@PathVariable("memberId") Long memberId, @RequestBody String groupName) {
//        groupService.saveGroup(groupName, memberId);
//        return "member/iAm";
//    }

    @GetMapping("/group/{memberId}/{groupsId}")
    public String updateGroupForm(@PathVariable("groupsId") Long groupsId, Model model) {
        GroupForm groupForm = new GroupForm();

        model.addAttribute("groupForm", groupForm);
        return "member/createGroupForm"; // form 줄 필요 없으면 삭제해도 되는 컨트롤러
    }

    @PostMapping("/group/{memberId}/{groupsId}")
    public String updateGroupName(@PathVariable("memberId") Long memberId, @PathVariable("groupsId") Long groupsId, @ModelAttribute("name") GroupForm name, Model model) {
        groupService.updateGroupName(groupsId, name.getName());
        model.addAttribute(memberRepository.findById(memberId));
        return "member/iAm"; // post 방식에 따라 바뀔 컨트롤러
    }
//    @PostMapping("/member/group/{groupsId}") //memberId가 필요함 memberid 반환해줘야함
//    public String updateGroupName(@PathVariable("groupsId") Long groupsId, String updateGroupName) {
//        groupService.updateGroupName(groupsId, updateGroupName);
//        return "/member/iAm";
//    } // post 방식으로 string 바로 받아올 수 있으면 이 컨트롤러 살리면 됨

    @DeleteMapping("/group/{memberId}/{groupId}")
    public String deleteGroup(@PathVariable("groupId") Long groupId) {
        groupService.deleteByGroupId(groupId);
        return "member/iAm";
    }
}