package openproject.where42.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import openproject.where42.member.domain.Locate;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchCadet {
    private String login;
    private String image_url;
    private String msg;
    private Locate locate;
    private int inOutStates; // 2 정보없음, 0 퇴근, 1 출근
    private boolean isFriend; // true 친구, false 아님
    private String location;
    private String inOutState;

    public SearchCadet() {}
//    public SearchCadet(String login) {
//        this.login = login;
//    } // 이거 왜 만들었더라..
}