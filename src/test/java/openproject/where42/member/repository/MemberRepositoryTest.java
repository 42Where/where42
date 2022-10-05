package openproject.where42.member.repository;

import openproject.where42.group.domain.Groups;
import openproject.where42.group.repository.GroupRepository;
import openproject.where42.groupMember.domain.GroupMember;
import openproject.where42.groupMember.repository.GroupMemberRepository;
import openproject.where42.member.domain.Member;
import openproject.where42.member.domain.enums.MemberLevel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class MemberRepositoryTest {
    @Autowired MemberRepository memberRepository;
    @Autowired GroupRepository groupRepository;
    @Autowired GroupMemberRepository groupMemberRepository;

    Map<String, Member> members = new HashMap<>();
    Map<String, Groups> groups = new HashMap<>();
    Map<String, GroupMember> groupMembers = new HashMap<>();

    @Test
    @Rollback(value = false)
    public void save() {
        Member jaebae = new Member("jaebae", MemberLevel.member);
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
        GroupMember sunghkim = new GroupMember("sunghkim", friends);
        GroupMember hyunjcho = new GroupMember("hyunjcho", friends);
        GroupMember sojoo = new GroupMember("sojoo", friends);
        GroupMember heeskim = new GroupMember("heeskim", friends);
        GroupMember jonkim = new GroupMember("jonkim", friends);
        GroupMember yyoo = new GroupMember("yyoo", friends);
        GroupMember dongchoi = new GroupMember("dongchoi", friends);
        GroupMember jaewchoi = new GroupMember("jaewchoi", friends);
        List<GroupMember> temp = List.of(sunghkim, hyunjcho, sojoo, heeskim, jonkim, yyoo, dongchoi, jaewchoi);
        groupMemberRepository.multiSave(temp);
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
        Boolean result = memberRepository.checkFriendByName("hyunjcho");
        System.out.println("hyunjcho = " + result);
    }

    @Test
    public void checkFriendByNameFalse() {
        save();
        Boolean result = memberRepository.checkFriendByName("jujo");
        System.out.println("jujo = " + result);
    }
}