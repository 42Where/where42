package openproject.where42.group.domain;

import lombok.Getter;
import lombok.Setter;
import openproject.where42.member.domain.Member;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Groups {
    @Id @GeneratedValue
    @Column(name = "group_id")
    private Long id;

    @Column(nullable = false)
    private String groupName;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member owner;

    @OneToMany(mappedBy = "group")
    private List<GroupMembers> groupMembers = new ArrayList<>();

    public Groups (String groupName, Member owner) { // 이렇게 생성자를 주면 id값이 제대로 발생할까?
        this.groupName = groupName;
        this.owner = owner;
    }

    public List<String> findAllFriends() { //groupMembers를 객체가 아닌 string으로 가지고 있는건? 아니면 이런식의 변환이 필요함, 프론트에 넘겨주는 것
        List<String> friends = new ArrayList<String>();
        if (groupMembers.isEmpty()) // 예외처리 해줘야 하나? 아니면 없으면 알아서 널인가?
            return null;
        for (GroupMembers f : groupMembers) {
            friends.add(f.getFriendName());
        }
        return friends;
    }

}
