package openproject.where42.group;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import openproject.where42.groupFriend.GroupFriend;
import openproject.where42.member.entity.Member;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     그룹 entity
 *     id: PK로 테이블의 고유값 [유니크]
 *     groupFriend: 1(groups) : N(groupFriend) 연결이 되어있는 테이블
 *     groupName: 널 값이 불가능하며 group의 이름을 담고있다
 *     owner:  1(member) : N(groups) 연결이 되어있는 테이블. member DB의 PK인 ID 값을 FK로 갖는다
 * </pre>
 * @versin 1.0
 * @author sunghkim
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SequenceGenerator(
        name = "GROUPS_SEQ_GENERATOR",
        sequenceName = "GROUPS_SEQ",
        initialValue = 1, allocationSize = 1
)
public class Groups {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GROUPS_SEQ")
    @Column(name = "group_id")
    private Long id;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    private List<GroupFriend> groupFriend = new ArrayList<GroupFriend>();

    @Column(nullable = false)
    private String groupName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member owner;

    /**
     * 멤버 그룹 생성자
     * @param groupName 생성할 그룹 이름
     * @param owner 그룹을 생성한 멤버
     * @since 1.0
     * @author sunghkim
     */
    public Groups (String groupName, Member owner) {
        this.owner = owner;
        this.groupName = groupName;
    }

    /**
     * 그룹 이름 갱신
     * @param groupName 갱신할 그룹 이름
     * @since 1.0
     * @author hyunjcho
     */
    public void updateGroupName(String groupName) {
        this.groupName = groupName;
    }
}
