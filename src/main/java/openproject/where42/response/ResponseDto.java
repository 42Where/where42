package openproject.where42.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
@Data
@AllArgsConstructor
@Builder
public class ResponseDto<T> {
    private int statusCode;
    private String responseMsg;
    private T data;

    public ResponseDto(final int statusCode, final String responseMsg) {
        this.statusCode = statusCode;
        this.responseMsg = responseMsg;
    }

    public static<T> ResponseDto<T> res(final int statusCode, final String responseMsg) {
        return res(statusCode, responseMsg, null);
    }

    public static<T> ResponseDto<T> res(final int statusCode, final String responseMsg, final T t) {
        return ResponseDto.<T>builder()
                .data(t)
                .statusCode(statusCode)
                .responseMsg(responseMsg)
                .build();
    }
}
