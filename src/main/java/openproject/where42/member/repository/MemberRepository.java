package openproject.where42.member.repository;

import lombok.RequiredArgsConstructor;
import openproject.where42.group.domain.Groups;
import openproject.where42.groupMember.domain.GroupMember;
import openproject.where42.member.domain.Member;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

    private final EntityManager em;

    public void save(Member member) {
        em.persist(member);
    }

    public Member findName(String name) {
        return em.createQuery("SELECT m FROM Member m WHERE m.name = :name", Member.class)
                .setParameter("name", name)
                .getSingleResult();
    }

    public Member findById(Long id) {
        return em.find(Member.class, id);
    }

//    public List<String> findNotIncludes(String groupName) {
//        List<Groups> groups = em.createQuery("select g from Groups g where g.groupName = :groupName or g.groupName = :friends", Groups.class)
//                .setParameter("groupName", groupName)
//                .setParameter("friends", "friends")
//                .getResultList();
//        List<GroupMember> friends = new ArrayList<>();
//        List<GroupMember> group = new ArrayList<>();
//        for (Groups group2 : groups) {
//            if (group2.getGroupName() == groupName)
//                group.addAll(group2.getGroupMembers());
//            else
//                friends.addAll(group2.getGroupMembers());
//        }
//        Map<String, Boolean> groupMap = new HashMap<>();
//        for (GroupMember groupMember : group) {
//            groupMap.put(groupMember.getFriendName(), true);
//        }
//        List<String> result = new ArrayList<>();
//        for (GroupMember groupMember : friends) {
//            if (groupMap.get(groupMember.getFriendName()) == null)
//                result.add(groupMember.getFriendName());
//        }
////        System.out.println("=========================");
////        for (String name : result)
////            System.out.println(name);
////        System.out.println("=========================");
//        return result;
//    }
}
