package openproject.where42.exception;

import openproject.where42.response.ErrResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(TakenSeatException.class)
    protected ResponseEntity defaultGroupNameException(TakenSeatException e) {
        return new ResponseEntity(ErrResponseDto.errorRes(e.getErrorCode(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
    }

    @ExceptionHandler(DefaultGroupNameException.class)
    protected ResponseEntity defaultGroupNameException(DefaultGroupNameException e) {
        return new ResponseEntity(ErrResponseDto.errorRes(e.getErrorCode(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
    }

    @ExceptionHandler(DuplicateGroupNameException.class)
    protected ResponseEntity handleDuplicateGroupNameException(DuplicateGroupNameException e) {
//        logger.error("handleHttpRequestMethodNotSupportedException", e); // 나중에 로거 남기는 거 해야됨
        return new ResponseEntity(ErrResponseDto.errorRes(e.getErrorCode(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
    }

    @ExceptionHandler(DefaultGroupNameException.class)
    protected ResponseEntity NotCustomGroupFriendException(NotCustomGroupFriend e) {
        return new ResponseEntity(ErrResponseDto.errorRes(e.getErrorCode(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
    }

}