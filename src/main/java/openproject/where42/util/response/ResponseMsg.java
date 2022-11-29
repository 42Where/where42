package openproject.where42.util.response;

public class ResponseMsg {
    public static final String LOGIN_SUCCESS = "로그인 성공";
    public static final String LOGOUT_SUCCESS = "로그아웃 성공";
    public static final String NO_COOKIE = "쿠키 없음";
    public static final String NO_SESSION = "세션 없음";
    public static final String LOGIN_FAIL = "로그인 실패";
    public static final String UNREGISTERED = "개인정보제공 동의 필요";
    public static final String CANNOT_ACCESS_AGREE = "개인정보제공 동의 화면 접근 불가";
    public static final String CREATE_MEMBER = "멤버 생성 성공";
    public static final String DELETE_MEMBER = "멤버 삭제 성공";
    public static final String SET_MSG = "상태메시지 저장 성공";
    public static final String SET_LOCATE = "자리 저장 성공";
    public static final String CREATE_GROUP = "그룹 생성 성공";
    public static final String NOT_TAKEN_SEAT = "자리 설정 가능";
    public static final String TAKEN_SEAT = "자리 정보 있음";
    public static final String OUT_STATE = "출근 상태 아님";
    public static final String CHANGE_GROUP_NAME = "그룹 이름 변경 성공";
    public static final String DELETE_GROUP = "그룹 삭제 성공";
    public static final String DUPLICATE_GROUP_NAME = "그룹 이름 중복";
    public static final String DEFAULT_GROUP_NAME = "사용자 설정 불가 이름";
    public static final String CREATE_GROUP_FRIEND = "친구 생성 성공";
    public static final String REGISTERED_GROUP_FRIEND = "이미 등록된 친구";
    public static final String ADD_FRIENDS_TO_GROUP = "그룹에 친구 추가 성공";
    public static final String DELETE_FRIENDS_FROM_GROUP = "그룹에서 친구 삭제 성공";
    public static final String DELETE_GROUP_FRIENDS = "친구 삭제 성공";
    public static final String JSON_DESERIALIZE_FAILED = "Json 매핑 실패";
    public static final String TOO_MANY_REQUEST = "Api 요청 횟수 추가";
    public static final String ADD_GROUPS_TO_FRIEND = "친구 그룹 추가 성공";
    public static final String NOT_FOUND_USER = "회원을 찾을 수 없습니다.";
    public static final String INTERNAL_SERVER_ERROR = "서버 내부 에러";
    public static final String DB_ERROR = "데이터베이스 에러";
}
