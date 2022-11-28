package openproject.where42.exception.customException;

import lombok.Getter;
import openproject.where42.util.response.ResponseMsg;
import openproject.where42.util.response.StatusCode;

@Getter
public class JsonDeserializeException extends RuntimeException {
    private int errorCode;

    public JsonDeserializeException() {
        super(ResponseMsg.JSON_DESERIALIZE_FAILED);
        this.errorCode = StatusCode.INTERNAL_SERVER_ERROR;
    }
}
