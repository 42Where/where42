package openproject.where42.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import openproject.where42.member.entity.Locate;

@Getter @Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchCadet {
    private String login;
    private Image image;
    private String msg;
    private Locate locate;
    private int inOrOut;
    private boolean isFriend;
    private String location;
}