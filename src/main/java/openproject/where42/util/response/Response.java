package openproject.where42.util.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class Response {
    private int statusCode;
    private String responseMsg;

    public static Response res(final int statusCode, final String responseMsg) {
        return Response.builder()
                .statusCode(statusCode)
                .responseMsg(responseMsg)
                .build();
    } // 객체가 아니라 builder를 사용하는 이유 찾아보기
}

