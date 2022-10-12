package openproject.where42.member.dto;

import lombok.Getter;
import lombok.Setter;
import openproject.where42.member.domain.enums.Planet;
import openproject.where42.member.domain.enums.Place;

import javax.validation.constraints.NotEmpty;

@Getter @Setter
public class LocateForm {
    @NotEmpty
    private Planet planet;
    @NotEmpty
    private int floor;
    @NotEmpty
    private int cluster;
    @NotEmpty
    private Place place;
}
