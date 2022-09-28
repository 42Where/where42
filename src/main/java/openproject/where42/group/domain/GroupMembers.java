package openproject.where42.group.domain;

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
    private Groups groups;

    @Column(nullable = false)
    private String friendName;
}
