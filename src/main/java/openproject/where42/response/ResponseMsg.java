package openproject.where42.response;

public class ResponseMsg {
    public static final String LOGIN_SUCCESS = "로그인 성공";
    public static final String LOGIN_FAIL = "로그인 실패";
    public static final String CREATE_MEMBER = "멤버 생성 성공";
    public static final String GET_MSG = "상태메시지 조회 성공";
    public static final String SET_MSG = "상태메시지 저장 성공";
    public static final String SET_LOCATE = "위치 저장 성공";
    public static final String CREATE_GROUP = "그룹 생성 성공";
    public static final String NOT_TAKEN_SEAT = "자리 설정 가능";
    public static final String TAKEN_SEAT = "자리 정보 있음";
    public static final String OUT_STATE = "출근 상태 아님";
    public static final String CHANGE_GROUP_NAME = "그룹 이름 변경 성공";
    public static final String DELETE_GROUP = "그룹 삭제 성공";
    public static final String DUPLICATE_GROUP_NAME = "그룹 이름 중복";
    public static final String DEFAULT_GROUP_NAME = "사용자 설정 불가 이름";
    public static final String CREATE_GROUP_FRIEND = "친구 생성 성공";
    public static final String ADD_FRIENDS_TO_GROUP = "그룹에 친구 추가 성공";
    public static final String DELETE_FRIENDS_FROM_GROUP = "그룹에서 친구 삭제 성공";
    public static final String DELETE_GROUP_FRIENDS = "친구 삭제 성공";
    public static final String ADD_GROUPS_TO_FRIEND = "친구 그룹 추가 성공";
    //    public static final String READ_USER = "회원 정보 조회 성공";
    public static final String NOT_FOUND_USER = "회원을 찾을 수 없습니다.";
//    public static final String CREATED_USER = "회원 가입 성공";
//    public static final String UPDATE_USER = "회원 정보 수정 성공";
//    public static final String DELETE_USER = "회원 탈퇴 성공";
    public static final String INTERNAL_SERVER_ERROR = "서버 내부 에러";
    public static final String DB_ERROR = "데이터베이스 에러";
}
