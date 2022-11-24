package openproject.where42.groupFriend.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import openproject.where42.group.entity.Groups;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Groups group;

    @Column(nullable = false)
    private String friendName;

    private String image;

    public GroupFriend(String friendName, Groups group) {
        this.friendName = friendName;
        this.group = group;
    }
}
