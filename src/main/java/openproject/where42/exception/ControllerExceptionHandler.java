package openproject.where42.exception;

import lombok.extern.slf4j.Slf4j;
import openproject.where42.exception.customException.*;
import openproject.where42.util.response.Response;
import openproject.where42.util.response.ResponseWithData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * <pre>
 *     throw된 exception 처리
 *     각 exception의 상세 정보는 customException 패키지에서 확인
 * </pre>
 * @see openproject.where42.exception.customException
 * @version 2.0
 * @author hyunjcho
 */
@ControllerAdvice
@Slf4j
public class ControllerExceptionHandler {

    @ExceptionHandler(UnregisteredMemberException.class)
    protected ResponseEntity handleUnregisteredException(UnregisteredMemberException e) {
        log.info("************** [UnregisteredException]이 발생하였습니다. **************");
        return new ResponseEntity(ResponseWithData.res(e.getErrorCode(), e.getMessage(), e.getSeoul42()), HttpStatus.valueOf(e.getErrorCode()));
    }

    @ExceptionHandler(SessionExpiredException.class)
    protected ResponseEntity handleSessionExpiredException(SessionExpiredException e) {
        log.info("************** [SessionExpiredException]이 발생하였습니다. **************");
        return new ResponseEntity(Response.res(e.getErrorCode(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
    }

    @ExceptionHandler(TokenExpiredException.class)
    protected ResponseEntity handeTokenExpiredException(TokenExpiredException e) {
        log.info("************** [TokenExpiredException]이 발생하였습니다. **************");
        return new ResponseEntity(Response.res(e.getErrorCode(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
    }

    @ExceptionHandler(CannotAccessAgreeException.class)
    protected ResponseEntity handleTooManyReqeustException(CannotAccessAgreeException e) {
        log.info("************** [TooManyReqeustException]이 발생하였습니다. **************");
        return new ResponseEntity(Response.res(e.getErrorCode(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
    }

    @ExceptionHandler(TakenSeatException.class)
    protected ResponseEntity handleTakenSeatException(TakenSeatException e) {
        log.info("************** [TakenSeatException]이 발생하였습니다. **************");
        return new ResponseEntity(Response.res(e.getErrorCode(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
    }

    @ExceptionHandler(OutStateException.class)
    protected ResponseEntity handleOutStateException(OutStateException e) {
        log.info("************** [OutStateException]이 발생하였습니다. **************");
        return new ResponseEntity(Response.res(e.getErrorCode(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
    }

    @ExceptionHandler(DefaultGroupNameException.class)
    protected ResponseEntity handleDefaultGroupNameException(DefaultGroupNameException e) {
        log.info("************** [DefaultGroupNameException]이 발생하였습니다. **************");
        return new ResponseEntity(Response.res(e.getErrorCode(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
    }

    @ExceptionHandler(DuplicateGroupNameException.class)
    protected ResponseEntity handleDuplicateGroupNameException(DuplicateGroupNameException e) {
        log.info("************** [DuplicateGroupNameException]이 발생하였습니다. **************");
        return new ResponseEntity(Response.res(e.getErrorCode(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
    }

    @ExceptionHandler(RegisteredFriendException.class)
    protected ResponseEntity handleRegisteredFriendException(RegisteredFriendException e) {
        log.info("************** [RegisteredFriendException]이 발생하였습니다. **************");
        return new ResponseEntity(Response.res(e.getErrorCode(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
    }

    @ExceptionHandler(JsonDeserializeException.class)
    protected ResponseEntity handleJsonDeserializeException(JsonDeserializeException e) {
        log.info("************** [JsonDeserializeException]이 발생하였습니다. **************");
        return new ResponseEntity(Response.res(e.getErrorCode(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
    }

    @ExceptionHandler(TooManyRequestException.class)
    protected ResponseEntity handleTooManyReqeustException(TooManyRequestException e) {
        log.info("************** [TooManyReqeustException]이 발생하였습니다. **************");
        return new ResponseEntity(Response.res(e.getErrorCode(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
    }

    @ExceptionHandler(BadRequestException.class)
    protected ResponseEntity handleBadRequestException(BadRequestException e) {
        log.info("************** [BadRequestException]이 발생하였습니다. **************");
        return new ResponseEntity(Response.res(e.getErrorCode(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
    }

    @ExceptionHandler(NotFoundException.class)
    protected ResponseEntity handleBadRequestException(NotFoundException e) {
        log.info("************** [NotFoundException]이 발생하였습니다. **************");
        return new ResponseEntity(Response.res(e.getErrorCode(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    protected ResponseEntity handleServiceUnavailableException(ServiceUnavailableException e) {
        log.info("************** [ServiceUnavailableException]이 발생하였습니다. **************");
        return new ResponseEntity(Response.res(e.getErrorCode(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
    }

    @ExceptionHandler(AdminLoginFailException.class)
    protected ResponseEntity handleAdminLoginFailException(AdminLoginFailException e) {
        log.info("************** [AdminLoginFailException]이 발생하였습니다. **************");
        return new ResponseEntity(Response.res(e.getErrorCode(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
    }
}