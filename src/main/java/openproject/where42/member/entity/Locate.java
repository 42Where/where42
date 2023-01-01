package openproject.where42.member.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import openproject.where42.member.entity.enums.Planet;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Locate {
    @Enumerated(EnumType.ORDINAL)
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

    public void updateLocate(Planet planet, int floor, int cluster, String spot) {
        this.planet = planet;
        this.floor = floor;
        this.cluster = cluster;
        this.spot = spot;
    }

    public static Locate parseLocate(String location) {
        if (location == null)
            return new Locate(null, 0, 0, null);
        int i = location.charAt(1) - '0';
        if ((i >= 1 && location.charAt(2) != '0') && i <= 6 || location.charAt(2) == 'X') {
            if (i <= 2)
                return new Locate(Planet.gaepo, 2, 0, location);
            else if (i <= 4)
                return new Locate(Planet.gaepo, 4, 0, location);
            else if (i <= 6)
                return new Locate(Planet.gaepo, 5, 0, location);
            else
                return new Locate(Planet.gaepo, 3, 0, location);
        } else if (i >= 7 && i <= 9)
            return new Locate(Planet.seocho, 0, i, location);
        return new Locate(Planet.seocho, 0, 10, location);
    }
}
