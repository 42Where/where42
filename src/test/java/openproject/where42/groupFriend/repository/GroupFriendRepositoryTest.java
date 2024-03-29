//package openproject.where42.groupFriend.repository;
//
//import openproject.where42.group.Groups;
//import openproject.where42.group.GroupRepository;
//import openproject.where42.groupFriend.GroupFriendRepository;
//import openproject.where42.groupFriend.GroupFriend;
//import openproject.where42.member.entity.Member;
//import openproject.where42.member.entity.enums.MemberLevel;
//import openproject.where42.member.MemberRepository;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.annotation.Rollback;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest
//@Transactional
//public class GroupFriendRepositoryTest {
//
//    @Autowired
//    MemberRepository memberRepository;
//    @Autowired
//    GroupRepository groupRepository;
//    @Autowired
//    GroupFriendRepository groupFriendRepository;
//
//    Map<String, Member> members = new HashMap<>();
//    Map<String, Groups> groups = new HashMap<>();
//    Map<String, GroupFriend> groupMembers = new HashMap<>();
//
//    @Test
//    @Rollback(value = false)
//    public void save() {
//        Member jaebae = new Member("jaebae", null, MemberLevel.member);
//        members.put("jaebae", jaebae);
//        memberRepository.save(jaebae);
//
//        Groups friends = new Groups("기본", jaebae);
//        Groups where42 = new Groups("where42", jaebae);
//        Groups study = new Groups("study", jaebae);
//        groups.put("friends", friends);
//        groups.put("where42", where42);
//        groups.put("study", study);
//        groupRepository.save(friends);
//        groupRepository.save(where42);
//        groupRepository.save(study);
//
//        GroupFriend sunghkim = new GroupFriend("sunghkim", friends);
//        GroupFriend hyunjcho = new GroupFriend("hyunjcho", friends);
//        GroupFriend sojoo = new GroupFriend("sojoo", friends);
//        GroupFriend heeskim = new GroupFriend("heeskim", friends);
//        GroupFriend jonkim = new GroupFriend("jonkim", friends);
//        GroupFriend yyoo = new GroupFriend("yyoo", friends);
//        GroupFriend dongchoi = new GroupFriend("dongchoi", friends);
//        GroupFriend jaewchoi = new GroupFriend("jaewchoi", friends);
//        groupMembers.put("sunghkim", sunghkim);
//        groupMembers.put("hyunjcho", hyunjcho);
//        groupMembers.put("sojoo", sojoo);
//        groupMembers.put("heeskim", heeskim);
//        groupMembers.put("jonkim", jonkim);
//        groupMembers.put("yyoo", yyoo);
//        groupMembers.put("dongchoi", dongchoi);
//        groupMembers.put("jaewchoi", jaewchoi);
//        groupFriendRepository.save(sunghkim);
//        groupFriendRepository.save(hyunjcho);
//        groupFriendRepository.save(sojoo);
//        groupFriendRepository.save(heeskim);
//        groupFriendRepository.save(jonkim);
//        groupFriendRepository.save(yyoo);
//        groupFriendRepository.save(dongchoi);
//        groupFriendRepository.save(jaewchoi);
//
//
//        GroupFriend sunghkim2 = new GroupFriend("sunghkim", where42);
//        GroupFriend hyunjcho2 = new GroupFriend("hyunjcho", where42);
//        GroupFriend sojoo2 = new GroupFriend("sojoo", where42);
//        GroupFriend heeskim2 = new GroupFriend("heeskim", where42);
//        groupMembers.put("sunghkim2", sunghkim2);
//        groupMembers.put("hyunjcho2", hyunjcho2);
//        groupMembers.put("sojoo2", sojoo2);
//        groupMembers.put("heeskim2", heeskim2);
//        groupFriendRepository.save(sunghkim2);
//        groupFriendRepository.save(hyunjcho2);
//        groupFriendRepository.save(sojoo2);
//        groupFriendRepository.save(heeskim2);
//
//        GroupFriend hyunjcho3 = new GroupFriend("hyunjcho", study);
//        GroupFriend dongchoi2 = new GroupFriend("dongchoi", study);
//        GroupFriend jaewchoi2 = new GroupFriend("jaewchoi", study);
//        groupMembers.put("hyunjcho3", hyunjcho3);
//        groupMembers.put("dongchoi2", dongchoi2);
//        groupMembers.put("jaewchoi2", jaewchoi2);
//        groupFriendRepository.save(hyunjcho3);
//        groupFriendRepository.save(dongchoi2);
//        groupFriendRepository.save(jaewchoi2);
//    }
//
//    @Test
//    // 존재하지 않은 friendName을 넣었을 때 모든 그룹들이 나오는 문제
//    public void notIncludeGroupByMemberAndFriendName() {
//        save();
//        List<String> groups = groupFriendRepository.notIncludeGroupByMemberAndFriendName(members.get("jaebae"), "jaewchoi");
//        for (String group : groups) {
//            System.out.println(group);
//        }
//    }
//
//    @Test
//    public void notIncludeFriendByGroup() {
//        save();
//        List<String> friends = groupFriendRepository.notIncludeFriendByGroup(members.get("jaebae"), groups.get("where42").getId());
//        for (String friend : friends) {
//            System.out.println(friend);
//        }
//    }
//
//    @Test
//    public void deleteGroupFriend() {
//
//    }
//
//    @Test
//    public void deleteGroupFriends() {
//
//    }
//
//    @Test
//    @Rollback(value = false)
//    public void deleteGroupFriendByGroupFriendId() {
//        save();
////        groupFriendRepository.deleteGroupFriendByGroupFriendId(groupMembers.get("hyunjcho3").getId());
//        groupFriendRepository.deleteGroupFriendsByGroupFriendId(List.of(groupMembers.get("hyunjcho3").getId(), groupMembers.get("dongchoi2").getId()));
//    }
//
//    @Test
//    public void findGroupFriendsByGroupId() {
//        save();
//        List<String> friends = groupFriendRepository.findGroupFriendsByGroupId(groups.get("where42").getId());
//        for (String friend : friends) {
//            System.out.println(friend);
//        }
//    }
//
//    @Test
//    public void findGroupFriendsByGroupIdEmpty() {
//        save();
//        List<String> friends = groupFriendRepository.findGroupFriendsByGroupId(groups.get("hello").getId());
//        for (String friend : friends) {
//            System.out.println(friend);
//        }
//    }
//
//    @Test
//    public void findAllGroupFriendByOwnerId() {
//        save();
//        List<GroupFriend> friends = groupFriendRepository.findAllGroupFriendByOwnerId(members.get("jaebae").getId());
//        for (GroupFriend name : friends) {
//            System.out.println(name.getFriendName());
//        }
//    }
//}