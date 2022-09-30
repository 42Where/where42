package openproject.where42.groupMember.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import openproject.where42.group.domain.Groups;

import javax.persistence.*;
import javax.swing.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SequenceGenerator(
        name = "GROUPMEMBERS_SEQ_GENERATOR",
        sequenceName = "GROUP_MEMBERS_SEQ",
        initialValue = 1, allocationSize = 1
)
public class GroupMember {

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GROUP_MEMBERS_SEQ")
    @Column(name = "group_member_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Groups group;

    @Column(nullable = false)
    private String friendName;

    public GroupMember(String friendName, Groups group) {
        this.friendName = friendName;
        this.group = group;
    }
}
