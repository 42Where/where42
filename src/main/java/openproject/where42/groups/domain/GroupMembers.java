package openproject.where42.groups.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
public class GroupMembers {
    @Id @GeneratedValue
    @Column(name = "group_member_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Groups group;

    @Column(nullable = false)
    private String friend_name;
}
