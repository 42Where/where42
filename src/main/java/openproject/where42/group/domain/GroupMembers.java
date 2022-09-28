package openproject.where42.group.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
@SequenceGenerator(
        name = "GROUPMEMBERS_SEQ_GENERATOR",
        sequenceName = "GROUP_MEMBERS_SEQ",
        initialValue = 1, allocationSize = 1
)
public class GroupMembers {

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GROUP_MEMBERS_SEQ")
    @Column(name = "group_member_id")
    private Long id;
    @ManyToOne
    @JoinColumn(name = "group_id")
    private Groups group;

    @Column(nullable = false)
    private String friendName;
}
