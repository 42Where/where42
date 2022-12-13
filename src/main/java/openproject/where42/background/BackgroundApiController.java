package openproject.where42.background;

import lombok.RequiredArgsConstructor;
import openproject.where42.util.Define;
import openproject.where42.util.response.Response;
import openproject.where42.util.response.ResponseMsg;
import openproject.where42.util.response.StatusCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BackgroundApiController {

    private final BackgroundService backgroundService;

    @GetMapping(Define.WHERE42_VERSION_PATH + "/incluster") // 서버 실행 시 자동 실행 방법..? 2주에 한 번 해줘야 하는 것들을 모아놓고 스케쥴러로 돌려도 좋고..
    public ResponseEntity findAllInClusterCadet() {
        backgroundService.updateAllInClusterCadet();
        return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.IN_CLUSTER), HttpStatus.OK);
    }

    @GetMapping(Define.WHERE42_VERSION_PATH + "/image/member")
    public ResponseEntity deleteImage() {
        backgroundService.deleteMemberImage();
        return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.GET_IMAGE_SUCCESS), HttpStatus.OK);
    }

    @GetMapping(Define.WHERE42_VERSION_PATH + "/image")
    public ResponseEntity getAllCadetImages() {
        backgroundService.getAllCadetImages(); // 에러 처리 확인
        return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.GET_IMAGE_SUCCESS), HttpStatus.OK);
    }
}
