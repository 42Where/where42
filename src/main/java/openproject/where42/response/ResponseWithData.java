package openproject.where42.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
@Data
@AllArgsConstructor
@Builder
public class ResponseWithData<T> {
    private int statusCode;
    private String responseMsg;
    private T data;

    public static<T> ResponseWithData<T> res(final int statusCode, final String responseMsg, final T t) {
        return ResponseWithData.<T>builder()
                .data(t)
                .statusCode(statusCode)
                .responseMsg(responseMsg)
                .build();
    }
}
