package openproject.where42.exception;

import openproject.where42.response.ErrResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(DuplicateGroupNameException.class)
    protected ResponseEntity handleDuplicateGroupNameException(DuplicateGroupNameException e) {
//        logger.error("handleHttpRequestMethodNotSupportedException", e);
        return new ResponseEntity(ErrResponseDto.errorRes(e.getErrorCode(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
    }
}