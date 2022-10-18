package openproject.where42.member.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import openproject.where42.member.domain.enums.Planet;

import javax.persistence.Embeddable;
import javax.persistence.Enumerated;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Locate {
    @Enumerated
    private Planet planet;

    private int floor;

    private int cluster;

    private String spot;

    public Locate (Planet planet, int floor, int cluster, String spot) {
        this.planet = planet;
        this.floor = floor;
        this.cluster = cluster;
        this.spot = spot;
    }
}
