package openproject.where42.group.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
@SequenceGenerator(
        name = "GROUPMEMBERS_SEQ_GENERATOR", // 재배야 그룹멤버스에서 s뗀걸루 클래스명 바꿨는데 이건 뭐 어케해야하는지 몰ㄹ라서.. 부탁한다..
        sequenceName = "GROUP_MEMBERS_SEQ",
        initialValue = 1, allocationSize = 1
)
public class GroupMember {

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GROUP_MEMBERS_SEQ")
    @Column(name = "group_member_id")
    private Long id;
    @ManyToOne
    @JoinColumn(name = "group_id")
    private Groups group;

    @Column(nullable = false)
    private String friendName;
}
