package openproject.where42.groupFriend;

import lombok.Getter;
import lombok.Setter;
import openproject.where42.flashData.FlashData;
import openproject.where42.member.entity.Locate;
import openproject.where42.member.entity.Member;

/**
 * <pre>
 *     친구 관련 api 반환용 DTO 클래스
 *     id: 친구 id
 *     name: 친구 intra Id
 *     img: 친구 img url
 *     msg: 친구 상태메시지
 *     locate: 친구 위치 정보
 *     inOrOut: 친구 출퇴근 정보
 *     eval: 친구 동료평가 정보 (ver2에 추가)
 * </pre>
 * @version 2.0
 * @see openproject.where42.groupFriend
 */
@Getter @Setter
public class GroupFriendDto {
	private Long id;
	private String name;
	private String img;
	private String msg;
	private Locate locate;
	private int inOrOut;
	private int eval;

	/**
	 * 친구가 멤버인 경우
	 * @param friend 친구의 멤버 정보
	 * @param id groupFriend DB에 등록된 친구의 id
	 * @since 1.0
	 * @author hyunjcho
	 */
	public GroupFriendDto(Member friend, Long id) {
		this.id = id;
		this.name = friend.getName();
		this.img = friend.getImg();
		this.msg = friend.getMsg();
		this.locate = friend.getLocate();
		this.inOrOut = friend.getInOrOut();
		this.eval = friend.getEvaling();
	}

	/**
	 * 친구가 멤버가 아닌 경우
	 * @param flash flashData 정보
	 * @param id groupFriend DB에 등록된 친구의 id
	 * @param img groupFriend DB에 등록된 친구의 img
	 * @since 1.0
	 * @author hyunjcho
	 */
	public GroupFriendDto(FlashData flash, Long id, String img) {
		this.id = id;
		this.name = flash.getName();
		this.img = img;
		this.locate = flash.getLocate();
		this.inOrOut = flash.getInOrOut();
	}
}