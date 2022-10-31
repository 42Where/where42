package openproject.where42.groupFriend.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import openproject.where42.group.domain.Groups;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SequenceGenerator(
        name = "GROUPFRIEND_SEQ_GENERATOR",
        sequenceName = "GROUP_FRIEND_SEQ",
        initialValue = 1, allocationSize = 1
)
public class GroupFriend {

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GROUP_FRIEND_SEQ")
    @Column(name = "group_friend_id")
    private Long id;

    // 재배님한테 말씀 드릴거!
    @Column(name = "intra_id")
    private Long intraId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Groups group;

    @Column(nullable = false)
    private String friendName;

    public GroupFriend(String friendName, Groups group) {
        this.friendName = friendName;
        this.group = group;
    }
}
