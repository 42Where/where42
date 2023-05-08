package openproject.where42.groupFriend;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import openproject.where42.group.Groups;

import javax.persistence.*;
import java.util.Date;

/**
 * <pre>
 *     그룹 entity
 *     id: PK로 테이블의 고유값 [유니크]
 *     group: 1(groups) : N(groupFriend) 연결이 되어있는 테이블. groups DB의 PK인 ID 값을 FK로 갖는다
 *     friendName: 널 값이 불가능하며 친구의 이름을 담고있다
 *     img: 이미지 주소인 URL을 갖는다
 *     addAt: Date 타입을 가지며 친구 추가한 시점의 시각을 저장한다
 * </pre>
 * 친구 entity
 * @author sunghkim
 * @versin 2.0
 * @see openproject.where42.groupFriend
 */
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
    private String signUpDate;
    @Column(name="add_at")
    private Date addAt;

    /**
     * <pre>
     *     친구 기본 그룹용 생성자
     *     ver2 업데이트 시 신규 네트워크망 확산 현황 확인을 위해 친구 등록일 및 친구의 피신 등록일 추가
     *     어디있니 가입 후 일자 경과별 신규로 추가되는 친구 데이터 확인용
     * </pre>
     * @param friendName 친구 이름
     * @param img 친구 이미지 주소, null일 경우 웨얼이 주소 저장
     * @param signUpdate 피신 등록일
     * @param group 친구 추가할 멤버의 기본 그룹
     * @since 1.0
     * @author sunghkim
     */
    public GroupFriend(String friendName, String img, String signUpdate, Groups group) {
        this.friendName = friendName;
        this.img = img;
        this.signUpDate = signUpdate;
        this.group = group;
        this.addAt = new Date();
    }

    /**
     * 친구 커스텀 그룹용 생성자
     * @param friendName 친구 이름
     * @param group 친구 추가할 멤버의 커스텀 그룹
     * @since 1.0
     * @author hyunjcho
     */
    public GroupFriend(String friendName, Groups group) {
        this.friendName = friendName;
        this.group = group;
    }

    public void updateSignUpDate(String date) {
        this.signUpDate = date;
    }
}
