package openproject.where42.groupMember.domain;

import openproject.where42.group.GroupService;
import openproject.where42.group.domain.Groups;
import openproject.where42.group.repository.GroupRepository;
import openproject.where42.groupMember.repository.GroupMemberRepository;
import openproject.where42.member.domain.Member;
import openproject.where42.member.domain.enums.MemberLevel;
import openproject.where42.member.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class GroupMemberTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    GroupMemberRepository groupMemberRepository;
    @Autowired
    GroupRepository groupRepository;

    @Autowired
    GroupService groupService;

    @Test
    @Rollback(value = false)
    public void test() {
        Member member = new Member("jaebae", MemberLevel.member);
        member.getGroups().add(new Groups("friends", member));
        member.getGroups().get(0).getGroupMembers().add(new GroupMember("sunghkim", member.getGroups().get(0)));
        memberRepository.save(member);

        Member member1 = memberRepository.findName("jaebae");
        System.out.println("======================================");
        System.out.println(member.getGroups().get(0).getGroupName());
        System.out.println(member.getGroups().get(0).getGroupMembers().get(0).getFriendName());
        System.out.println("======================================");

        Groups groups = groupRepository.findById(Long.valueOf(1));
        //System.out.println(groups.getGroupName());

        Groups groups1 = groupRepository.findByName("friends");
        System.out.println(groups1.getGroupName());

    }

    @Test
    @Rollback(value = false)
    public void findNotIncluesTest() {
        Member member = new Member("jaebae", MemberLevel.member);
        Groups friends = new Groups("friends", member);
        Groups where42 = new Groups("where42", member);
        member.getGroups().add(friends);
        member.getGroups().add(where42);

        GroupMember hyunjcho1 = new GroupMember("hyunjcho", friends);
        GroupMember sunghkim1 = new GroupMember("sunghkim", friends);
        GroupMember sojoo1 = new GroupMember("sojoo", friends);
        GroupMember heeskim1 = new GroupMember("heeskim", friends);
        GroupMember jonkim1 = new GroupMember("jonkim", friends);

        friends.getGroupMembers().add(hyunjcho1);
        friends.getGroupMembers().add(sunghkim1);
        friends.getGroupMembers().add(sojoo1);
        friends.getGroupMembers().add(heeskim1);
        friends.getGroupMembers().add(jonkim1);

        GroupMember hyunjcho2 = new GroupMember("hyunjcho", where42);
        GroupMember sunghkim2 = new GroupMember("sunghkim", where42);
        GroupMember sojoo2 = new GroupMember("sojoo", where42);

        where42.getGroupMembers().add(hyunjcho2);
        where42.getGroupMembers().add(sunghkim2);
        where42.getGroupMembers().add(sojoo2);

        memberRepository.save(member);

        memberRepository.findNotIncludes("where42");
    }

}