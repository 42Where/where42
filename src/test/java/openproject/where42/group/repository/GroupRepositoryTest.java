//package openproject.where42.group.repository;
//
//import openproject.where42.group.GroupRepository;
//import openproject.where42.group.entity.Groups;
//import openproject.where42.groupFriend.entity.GroupFriend;
//import openproject.where42.groupFriend.GroupFriendRepository;
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
//public class GroupRepositoryTest {
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
//        Groups friends = new Groups("friends", jaebae);
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
//    @Rollback(value = false)
//    public void deleteGroupwhere42() {
//        save();
//        groupRepository.deleteByGroupId(groups.get("where42").getId());
//    }
//
//    @Test
//    @Rollback(value = false)
//    public void deleteGroupfriends() { // friends는 삭제되어서는 안되는데 잘 동작함. 처리가 필요할 듯?
//        save();
//        groupRepository.deleteByGroupId(groups.get("friends").getId());
//    }
//
//    @Test
//    @Rollback(value = false)
//    public void deleteGroupstudy() { // 존재하지 않은 그룹을 삭제할 떄 IllegalArgumentException가 발생하는데, 신경써야하는가? 발생할 여지가 없나?
//        groupRepository.deleteByGroupId(groups.get("study").getId());
//    }
//
//    @Test
//    public void findById() {
//        save();
//        Groups group = groupRepository.findById(groups.get("friends").getId());
//        System.out.println(group.getGroupName());
//    }
//
//    @Test
//    public void findByOwnerIdAndName() {
//        save();
//        Groups group = groupRepository.findByOwnerIdAndName(members.get("jaebae").getId(), "friends");
//        System.out.println(group.getGroupName());
//    }
//
//    @Test
//    // NullPointerException 확인해야할 듯
//    // 이거는 기능 상 모든 멤버에는 필수적으로 friends와 starred 그룹이 존재하기 때문에 신경 안써도 될듯?
//    public void findByOwnerIdAndNameFalse() {
//        save();
//        Groups group = groupRepository.findByOwnerIdAndName(members.get("jaebae2").getId(), "friends");
//        System.out.println(group.getGroupName());
//    }
//
//    @Test
//    // https://m.blog.naver.com/tmondev/220393974518 확인요망
//    public void findGroupsByOwnerId() {
//        save();
//        List<Groups> groups = groupRepository.findGroupsByOwnerId(members.get("jaebae").getId());
//        for (Groups group : groups) {
//            System.out.println(group.getGroupName());
//        }
//    }
//
//    @Test
//    public void isGroupNameInOwner() {
//        save();
//        System.out.println(groupRepository.isGroupNameInOwner(members.get("jaebae").getId(), "friends"));
//        System.out.println(groupRepository.isGroupNameInOwner(members.get("jaebae").getId(), "starred"));
//        System.out.println(groupRepository.isGroupNameInOwner(members.get("jaebae").getId(), "study"));
//
//    }
//}