package openproject.where42.member.repository;

import openproject.where42.group.domain.Groups;
import openproject.where42.group.GroupRepository;
import openproject.where42.groupFriend.domain.GroupFriend;
import openproject.where42.groupFriend.GroupFriendRepository;
import openproject.where42.member.MemberRepository;
import openproject.where42.member.domain.Member;
import openproject.where42.member.domain.enums.MemberLevel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class MemberRepositoryTest {
    @Autowired
    MemberRepository memberRepository;
    @Autowired GroupRepository groupRepository;
    @Autowired GroupFriendRepository groupFriendRepository;

    Map<String, Member> members = new HashMap<>();
    Map<String, Groups> groups = new HashMap<>();
    Map<String, GroupFriend> groupMembers = new HashMap<>();

    @Test
    @Rollback(value = false)
    public void save() {
        Member jaebae = new Member("jaebae", null, MemberLevel.member);
        members.put("jaebae", jaebae);
        memberRepository.save(jaebae);
        Groups friends = new Groups("friends", jaebae);
        groups.put("friends", friends);
        groupRepository.save(friends);
        Groups where42 = new Groups("where42", jaebae);
        groups.put("where42", where42);
        groupRepository.save(where42);
        Groups study = new Groups("study", jaebae);
        groups.put("study", study);
        groupRepository.save(study);
        GroupFriend sunghkim = new GroupFriend("sunghkim", friends);
        GroupFriend hyunjcho = new GroupFriend("hyunjcho", friends);
        GroupFriend sojoo = new GroupFriend("sojoo", friends);
        GroupFriend heeskim = new GroupFriend("heeskim", friends);
        GroupFriend jonkim = new GroupFriend("jonkim", friends);
        GroupFriend yyoo = new GroupFriend("yyoo", friends);
        GroupFriend dongchoi = new GroupFriend("dongchoi", friends);
        GroupFriend jaewchoi = new GroupFriend("jaewchoi", friends);
        List<GroupFriend> temp = List.of(sunghkim, hyunjcho, sojoo, heeskim, jonkim, yyoo, dongchoi, jaewchoi);
        groupFriendRepository.multiSave(temp);
    }

    @Test
    public void findName() {
        save();
        Member member = memberRepository.findByName("jaebae");
        System.out.println(member.getName());
    }

    @Test
    public void findById() {
        save();
        Member member = memberRepository.findById(Long.valueOf(1));
        System.out.println(member.getName());
    }

    @Test
    public void findByIdFalse() {
        // Optional 씌워야할듯? 현진님 API 끝나면 이야기 해야할듯
        save();
        Member member = memberRepository.findById(Long.valueOf(2));
        System.out.println(member.getName());
    }

    @Test
    public void checkMemberByNameTrue() {
        save();
        Boolean result = memberRepository.checkMemberByName("jaebae");
        System.out.println(result);
    }

    @Test
    public void checkMemberByNameFalse() {
        Boolean result = memberRepository.checkMemberByName("jaebae");
        System.out.println(result);
    }

    @Test
    public void checkFriendByNameTrue() {
        save();
        Boolean result = memberRepository.checkMemberByName("hyunjcho");
        System.out.println("hyunjcho = " + result);
    }

    @Test
    public void checkFriendByNameFalse() {
        save();
        Boolean result = memberRepository.checkMemberByName("jujo");
        System.out.println("jujo = " + result);
    }

    @Test
    public void checkFriendByMemberIdAndName() {
        save();
        Boolean result = memberRepository.checkFriendByMemberIdAndName(members.get("jaebae").getId(), "sunghkim");
        System.out.println(result);
    }
}