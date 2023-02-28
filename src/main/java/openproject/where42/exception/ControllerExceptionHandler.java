package openproject.where42.exception;

import lombok.extern.slf4j.Slf4j;
import openproject.where42.exception.customException.*;
import openproject.where42.util.response.Response;
import openproject.where42.util.response.ResponseWithData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class ControllerExceptionHandler {

    @ExceptionHandler(UnregisteredMemberException.class)
    protected ResponseEntity handleUnregisteredException(UnregisteredMemberException e) {
        log.info("************** [handleUnregisteredException]이 발생하였습니다. **************");
        return new ResponseEntity(ResponseWithData.res(e.getErrorCode(), e.getMessage(), e.getSeoul42()), HttpStatus.valueOf(e.getErrorCode()));
    }

    @ExceptionHandler(SessionExpiredException.class)
    protected ResponseEntity handleSessionExpiredException(SessionExpiredException e) {
        log.info("************** [handleSessionExpiredException]이 발생하였습니다. **************");
        return new ResponseEntity(Response.res(e.getErrorCode(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
    }

    @ExceptionHandler(TokenExpiredException.class)
    protected ResponseEntity handeTokenExpiredException(TokenExpiredException e) {
        log.info("************** [handelTokenExpiredException]이 발생하였습니다. **************");
        return new ResponseEntity(Response.res(e.getErrorCode(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
    }

    @ExceptionHandler(CannotAccessAgreeException.class)
    protected ResponseEntity handleTooManyReqeustException(CannotAccessAgreeException e) {
        log.info("************** [handleTooManyReqeustException]이 발생하였습니다. **************");
        return new ResponseEntity(Response.res(e.getErrorCode(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
    }

    @ExceptionHandler(TakenSeatException.class)
    protected ResponseEntity handleTakenSeatException(TakenSeatException e) {
        log.info("************** [handleTakenSeatException]이 발생하였습니다. **************");
        return new ResponseEntity(Response.res(e.getErrorCode(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
    }

    @ExceptionHandler(OutStateException.class)
    protected ResponseEntity handleOutStateException(OutStateException e) {
        log.info("************** [handleOutStateException]이 발생하였습니다. **************");
        return new ResponseEntity(Response.res(e.getErrorCode(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
    }

    @ExceptionHandler(DefaultGroupNameException.class)
    protected ResponseEntity handleDefaultGroupNameException(DefaultGroupNameException e) {
        log.info("************** [handleDefaultGroupNameException]이 발생하였습니다. **************");
        return new ResponseEntity(Response.res(e.getErrorCode(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
    }

    @ExceptionHandler(DuplicateGroupNameException.class)
    protected ResponseEntity handleDuplicateGroupNameException(DuplicateGroupNameException e) {
        log.info("************** [handleDuplicateGroupNameException]이 발생하였습니다. **************");
        return new ResponseEntity(Response.res(e.getErrorCode(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
    }

    @ExceptionHandler(RegisteredFriendException.class)
    protected ResponseEntity handleRegisteredFriendException(RegisteredFriendException e) {
        log.info("************** [handleRegisteredFriendException]이 발생하였습니다. **************");
        return new ResponseEntity(Response.res(e.getErrorCode(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
    }

    @ExceptionHandler(JsonDeserializeException.class)
    protected ResponseEntity handleJsonDeserializeException(JsonDeserializeException e) {
        log.info("************** [handleJsonDeserializeException]이 발생하였습니다. **************");
        return new ResponseEntity(Response.res(e.getErrorCode(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
    }

    @ExceptionHandler(TooManyRequestException.class)
    protected ResponseEntity handleTooManyReqeustException(TooManyRequestException e) {
        log.info("************** [handleTooManyReqeustException]이 발생하였습니다. **************");
        return new ResponseEntity(Response.res(e.getErrorCode(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
    }

    @ExceptionHandler(BadRequestException.class)
    protected ResponseEntity handleBadRequestException(BadRequestException e) {
        log.info("************** [handleBadRequestException]이 발생하였습니다. **************");
        return new ResponseEntity(Response.res(e.getErrorCode(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
    }
}