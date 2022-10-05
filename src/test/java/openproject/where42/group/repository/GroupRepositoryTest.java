package openproject.where42.group.repository;

import openproject.where42.group.domain.Groups;
import openproject.where42.groupMember.domain.GroupMember;
import openproject.where42.groupMember.repository.GroupMemberRepository;
import openproject.where42.member.domain.Member;
import openproject.where42.member.domain.enums.MemberLevel;
import openproject.where42.member.repository.MemberRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class GroupRepositoryTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    GroupRepository groupRepository;
    @Autowired
    GroupMemberRepository groupMemberRepository;

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
        Groups where42 = new Groups("where42", jaebae);
        Groups study = new Groups("study", jaebae);
        groups.put("friends", friends);
        groups.put("where42", where42);
        groups.put("study", study);
        groupRepository.save(friends);
        groupRepository.save(where42);
        groupRepository.save(study);

        GroupMember sunghkim = new GroupMember("sunghkim", friends);
        GroupMember hyunjcho = new GroupMember("hyunjcho", friends);
        GroupMember sojoo = new GroupMember("sojoo", friends);
        GroupMember heeskim = new GroupMember("heeskim", friends);
        GroupMember jonkim = new GroupMember("jonkim", friends);
        GroupMember yyoo = new GroupMember("yyoo", friends);
        GroupMember dongchoi = new GroupMember("dongchoi", friends);
        GroupMember jaewchoi = new GroupMember("jaewchoi", friends);
        groupMembers.put("sunghkim", sunghkim);
        groupMembers.put("hyunjcho", hyunjcho);
        groupMembers.put("sojoo", sojoo);
        groupMembers.put("heeskim", heeskim);
        groupMembers.put("jonkim", jonkim);
        groupMembers.put("yyoo", yyoo);
        groupMembers.put("dongchoi", dongchoi);
        groupMembers.put("jaewchoi", jaewchoi);
        List<GroupMember> temp = List.of(sunghkim, hyunjcho, sojoo, heeskim, jonkim, yyoo, dongchoi, jaewchoi);
        groupMemberRepository.multiSave(temp);

        GroupMember sunghkim2 = new GroupMember("sunghkim", where42);
        GroupMember hyunjcho2 = new GroupMember("hyunjcho", where42);
        GroupMember sojoo2 = new GroupMember("sojoo", where42);
        GroupMember heeskim2 = new GroupMember("heeskim", where42);
        groupMembers.put("sunghkim2", sunghkim2);
        groupMembers.put("hyunjcho2", hyunjcho2);
        groupMembers.put("sojoo2", sojoo2);
        groupMembers.put("heeskim2", heeskim2);
        List<GroupMember> temp2 = List.of(sunghkim2, hyunjcho2, sojoo2, heeskim2);
        groupMemberRepository.multiSave(temp2);

        GroupMember hyunjcho3 = new GroupMember("hyunjcho", study);
        GroupMember dongchoi2 = new GroupMember("dongchoi", study);
        GroupMember jaewchoi2 = new GroupMember("jaewchoi", study);
        groupMembers.put("hyunjcho3", hyunjcho3);
        groupMembers.put("dongchoi2", dongchoi2);
        groupMembers.put("jaewchoi2", jaewchoi2);
        List<GroupMember> temp3 = List.of(hyunjcho3, dongchoi2, jaewchoi2);
        groupMemberRepository.multiSave(temp3);
    }

    @Test
    @Rollback(value = false)
    public void deleteGroupwhere42() {
        save();
        groupRepository.deleteGroup(groups.get("where42"));
    }

    @Test
    @Rollback(value = false)
    public void deleteGroupfriends() { // friends는 삭제되어서는 안되는데 잘 동작함. 처리가 필요할 듯?
        save();
        groupRepository.deleteGroup(groups.get("friends"));
    }

    @Test
    @Rollback(value = false)
    public void deleteGroupstudy() { // 존재하지 않은 그룹을 삭제할 떄 IllegalArgumentException가 발생하는데, 신경써야하는가? 발생할 여지가 없나?
        groupRepository.deleteGroup(groups.get("study"));
    }

    @Test
    public void findById() {
        save();
        Groups group = groupRepository.findById(groups.get("friends").getId());
        System.out.println(group.getGroupName());
    }

    @Test
    public void findByName() {
        save();
        Groups group = groupRepository.findByName("friends");
        System.out.println(group.getGroupName());
    }

    @Test
    public void findGroupsByOwnerName() {
        save();
        List<String> groupsList = groupRepository.findGroupsByOwnerName("jaebae");
        System.out.println(groupsList.toString());
    }
}