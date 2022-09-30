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
@Getter
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

    public Groups (String groupName) { // 이렇게 생성자를 주면 id값이 제대로 발생할까?
        this.groupName = groupName;
    }

    public void updateGroupName(String groupName) {
        this.groupName = groupName;
    }
}
