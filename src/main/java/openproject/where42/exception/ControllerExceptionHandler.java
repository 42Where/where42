package openproject.where42.exception;

import openproject.where42.exception.customException.*;
import openproject.where42.util.response.Response;
import openproject.where42.util.response.ResponseWithData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(UnregisteredMemberException.class)
    protected ResponseEntity handleUnregisteredException(UnregisteredMemberException e) {
        return new ResponseEntity(ResponseWithData.res(e.getErrorCode(), e.getMessage(), e.getSeoul42()), HttpStatus.valueOf(e.getErrorCode()));
    }

    @ExceptionHandler(SessionExpiredException.class)
    protected ResponseEntity handleSessionExpiredException(SessionExpiredException e) {
        return new ResponseEntity(Response.res(e.getErrorCode(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
    }

    @ExceptionHandler(CookieExpiredException.class)
    protected ResponseEntity handelCookieExpiredException(CookieExpiredException e) {
        return new ResponseEntity(Response.res(e.getErrorCode(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
    }

    @ExceptionHandler(TakenSeatException.class)
    protected ResponseEntity handleTakenSeatException(TakenSeatException e) {
        return new ResponseEntity(Response.res(e.getErrorCode(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
    }

    @ExceptionHandler(OutStateException.class)
    protected ResponseEntity handleOutStateException(OutStateException e) {
        return new ResponseEntity(Response.res(e.getErrorCode(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
    }

    @ExceptionHandler(DefaultGroupNameException.class)
    protected ResponseEntity handleDefaultGroupNameException(DefaultGroupNameException e) {
        return new ResponseEntity(Response.res(e.getErrorCode(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
    }

    @ExceptionHandler(DuplicateGroupNameException.class)
    protected ResponseEntity handleDuplicateGroupNameException(DuplicateGroupNameException e) {
//        logger.error("handleHttpRequestMethodNotSupportedException", e); // 나중에 로거 남기는 거 해야됨
        return new ResponseEntity(Response.res(e.getErrorCode(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
    }

    @ExceptionHandler(RegisteredFriendException.class)
    protected ResponseEntity handleRegisteredFriendException(RegisteredFriendException e) {
        return new ResponseEntity(Response.res(e.getErrorCode(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
    }

    @ExceptionHandler(JsonDeserializeException.class)
    protected ResponseEntity handleJsonDeserializeException(JsonDeserializeException e) {
        return new ResponseEntity(Response.res(e.getErrorCode(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
    }

    @ExceptionHandler(TooManyRequestException.class)
    protected ResponseEntity handleTooManyReqeustException(TooManyRequestException e) {
        return new ResponseEntity(Response.res(e.getErrorCode(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
    }
}