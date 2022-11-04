package openproject.where42.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrResponseDto {
    private int statusCode;
    private String responseMsg;

    public static ErrResponseDto errorRes(final int statusCode, final String responseMsg) {
        return ErrResponseDto.builder()
                .statusCode(statusCode)
                .responseMsg(responseMsg)
                .build();
    } // 객체가 아니라 builder를 사용하는 이유 찾아보기
}

