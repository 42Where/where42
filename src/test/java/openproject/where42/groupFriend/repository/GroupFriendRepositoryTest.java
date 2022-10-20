package openproject.where42.groupFriend.repository;

import openproject.where42.group.domain.Groups;
import openproject.where42.group.repository.GroupRepository;
import openproject.where42.groupFriend.domain.GroupFriend;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class GroupFriendRepositoryTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    GroupRepository groupRepository;
    @Autowired
    GroupFriendRepository groupFriendRepository;

    Map<String, Member> members = new HashMap<>();
    Map<String, Groups> groups = new HashMap<>();
    Map<String, GroupFriend> groupMembers = new HashMap<>();

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

        GroupFriend sunghkim = new GroupFriend("sunghkim", friends);
        GroupFriend hyunjcho = new GroupFriend("hyunjcho", friends);
        GroupFriend sojoo = new GroupFriend("sojoo", friends);
        GroupFriend heeskim = new GroupFriend("heeskim", friends);
        GroupFriend jonkim = new GroupFriend("jonkim", friends);
        GroupFriend yyoo = new GroupFriend("yyoo", friends);
        GroupFriend dongchoi = new GroupFriend("dongchoi", friends);
        GroupFriend jaewchoi = new GroupFriend("jaewchoi", friends);
        groupMembers.put("sunghkim", sunghkim);
        groupMembers.put("hyunjcho", hyunjcho);
        groupMembers.put("sojoo", sojoo);
        groupMembers.put("heeskim", heeskim);
        groupMembers.put("jonkim", jonkim);
        groupMembers.put("yyoo", yyoo);
        groupMembers.put("dongchoi", dongchoi);
        groupMembers.put("jaewchoi", jaewchoi);
        List<GroupFriend> temp = List.of(sunghkim, hyunjcho, sojoo, heeskim, jonkim, yyoo, dongchoi, jaewchoi);
        groupFriendRepository.multiSave(temp);

        GroupFriend sunghkim2 = new GroupFriend("sunghkim", where42);
        GroupFriend hyunjcho2 = new GroupFriend("hyunjcho", where42);
        GroupFriend sojoo2 = new GroupFriend("sojoo", where42);
        GroupFriend heeskim2 = new GroupFriend("heeskim", where42);
        groupMembers.put("sunghkim2", sunghkim2);
        groupMembers.put("hyunjcho2", hyunjcho2);
        groupMembers.put("sojoo2", sojoo2);
        groupMembers.put("heeskim2", heeskim2);
        List<GroupFriend> temp2 = List.of(sunghkim2, hyunjcho2, sojoo2, heeskim2);
        groupFriendRepository.multiSave(temp2);

        GroupFriend hyunjcho3 = new GroupFriend("hyunjcho", study);
        GroupFriend dongchoi2 = new GroupFriend("dongchoi", study);
        GroupFriend jaewchoi2 = new GroupFriend("jaewchoi", study);
        groupMembers.put("hyunjcho3", hyunjcho3);
        groupMembers.put("dongchoi2", dongchoi2);
        groupMembers.put("jaewchoi2", jaewchoi2);
        List<GroupFriend> temp3 = List.of(hyunjcho3, dongchoi2, jaewchoi2);
        groupFriendRepository.multiSave(temp3);
    }

    @Test
    // 존재하지 않은 friendName을 넣었을 때 모든 그룹들이 나오는 문제
    public void notIncludeGroupByMemberAndFriendName() {
        save();
        List<String> groups = groupFriendRepository.notIncludeGroupByMemberAndFriendName(members.get("jaebae"), "jaewchoi");
        for (String group : groups) {
            System.out.println(group);
        }
    }

    @Test
    public void notIncludeFriendByGroup() {
        save();
        List<String> friends = groupFriendRepository.notIncludeFriendByGroup(members.get("jaebae"), groups.get("where42").getId());
        for (String friend : friends) {
            System.out.println(friend);
        }
    }

    @Test
    public void deleteGroupFriend() {

    }

    @Test
    public void deleteGroupFriends() {

    }

    @Test
    public void deleteFriendsGroupByName() {

    }

    @Test
    // sort할 때 굳이 stream을 안쓰는 방향으로 가야할듯?
    public void findGroupFriendsByGroupId() {
        save();
        List<String> friends = groupFriendRepository.findGroupFriendsByGroupId(groups.get("where42").getId());
        for (String friend : friends) {
            System.out.println(friend);
        }
    }

    @Test
    public void findAllGroupFriendByOwnerId() {
        save();
        List<String> friends = groupFriendRepository.findAllGroupFriendByOwnerId(members.get("jaebae").getId());
        System.out.println(friends);
    }
}