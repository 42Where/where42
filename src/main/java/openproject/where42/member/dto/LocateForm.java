package openproject.where42.member.dto;

import lombok.Getter;
import lombok.Setter;
import openproject.where42.member.domain.enums.Cluster;
import openproject.where42.member.domain.enums.Floor;
import openproject.where42.member.domain.enums.Locate;

import javax.validation.constraints.NotEmpty;

@Getter @Setter
public class LocateForm {
    @NotEmpty
    private Cluster cluster;
    @NotEmpty
    private Floor floor;
    @NotEmpty
    private Locate locate;
}
