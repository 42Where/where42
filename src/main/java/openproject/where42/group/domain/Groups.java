package openproject.where42.group.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import openproject.where42.groupMember.domain.GroupMember;
import openproject.where42.member.domain.Member;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SequenceGenerator(
        name = "GROUPS_SEQ_GENERATOR",
        sequenceName = "GROUPS_SEQ",
        initialValue = 1, allocationSize = 1
)
public class Groups {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GROUPS_SEQ")
    @Column(name = "group_id")
    private Long id;

    @Column(nullable = false)
    private String groupName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member owner;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupMember> groupMembers = new ArrayList<>();

    public Groups (String groupName, Member owner) { // 이렇게 생성자를 주면 id값이 제대로 발생할까?
        this.groupName = groupName;
        this.owner = owner;
    }

    public List<String> findAllFriends() { //groupMembers를 객체가 아닌 string으로 가지고 있는건? 아니면 이런식의 변환이 필요함, 프론트에 넘겨주는 것
        List<String> friends = new ArrayList<String>();
        if (groupMembers.isEmpty()) // 예외처리 해줘야 하나? 아니면 없으면 알아서 널인가?
            return null;
        for (GroupMember f : groupMembers) {
            friends.add(f.getFriendName());
        }
        return friends;
    }

}
