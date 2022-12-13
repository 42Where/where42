package openproject.where42.groupFriend;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import openproject.where42.group.Groups;

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

    private String img;

    public GroupFriend(String friendName, String img, Groups group) {
        this.friendName = friendName;
        this.img = img;
        this.group = group;
    }

    public GroupFriend(String friendName, Groups group) {
        this.friendName = friendName;
        this.group = group;
    }
}
