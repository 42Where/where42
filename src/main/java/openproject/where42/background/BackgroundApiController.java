package openproject.where42.background;

import lombok.RequiredArgsConstructor;
import openproject.where42.flashData.FlashDataRepository;
import openproject.where42.token.TokenRepository;
import openproject.where42.util.Define;
import openproject.where42.util.response.Response;
import openproject.where42.util.response.ResponseMsg;
import openproject.where42.util.response.StatusCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BackgroundApiController {

    private final BackgroundService backgroundService;
    private final FlashDataRepository flashDataRepository;
    private final TokenRepository tokenRepository;

    @PostMapping(Define.WHERE42_VERSION_PATH + "/hane")
    public ResponseEntity insertHane() {
        tokenRepository.insertHane();
        return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.HANE_SUCCESS), HttpStatus.OK);
    }

    @DeleteMapping(Define.WHERE42_VERSION_PATH + "/image/member") // 이미지 디비 날리면 굳이 싶긴 하다?
    public ResponseEntity deleteImage() {
        backgroundService.deleteMemberImage();
        return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.GET_IMAGE_SUCCESS), HttpStatus.OK);
    }
    // 여기에 어드민 멤버 레벨 조건 넣기

    @GetMapping(Define.WHERE42_VERSION_PATH + "/incluster") // 서버 실행 시 자동 실행 방법..?
    public ResponseEntity findAllInClusterCadet() {
        backgroundService.updateAllInClusterCadet();
        return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.IN_CLUSTER), HttpStatus.OK);
    }

    @GetMapping(Define.WHERE42_VERSION_PATH + "/image")
    public ResponseEntity getAllCadetImages() {
        backgroundService.getAllCadetImages(); // 에러 처리 확인
        return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.GET_IMAGE_SUCCESS), HttpStatus.OK);
    }

    @DeleteMapping(Define.WHERE42_VERSION_PATH + "/flash")
    public ResponseEntity resetFlash() {
        flashDataRepository.resetFlash();
        return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.RESET_FLASH), HttpStatus.OK);
    }
}