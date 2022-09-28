package openproject.where42.group.domain;

import lombok.Getter;
import lombok.Setter;
import openproject.where42.group.dto.GroupForm;
import openproject.where42.member.domain.Member;
import org.springframework.web.bind.annotation.PostMapping;

import javax.persistence.*;

@Entity
@Getter @Setter
public class GroupMember {
    @Id @GeneratedValue
    @Column(name = "group_member_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Groups groups;

    @Column(nullable = false)
    private String friendName;
}
