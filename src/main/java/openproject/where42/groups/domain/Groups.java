package openproject.where42.groups.domain;

import lombok.Getter;
import lombok.Setter;
import openproject.where42.members.domain.Members;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Groups {
    @Id @GeneratedValue
    @Column(name = "group_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Members owner;

    @OneToMany(mappedBy = "group")
    private List<GroupMembers> groupMembers = new ArrayList<>();

    @Column(nullable = false)
    private String groupName;
}
