package openproject.where42.member.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import openproject.where42.member.entity.enums.Planet;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * 여기 써죠~
 */
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Locate {
    @Enumerated(EnumType.ORDINAL)
    private Planet planet;
    private int floor;
    private int cluster;
    private String spot;

    /**
     * locate 생성
     * @param planet 개포, 서초
     * @param floor 개포 층수
     * @param cluster 서초 클러스터
     * @param spot 자리
     * @since 1.0
     * @author sunghkim
     */
    public Locate (Planet planet, int floor, int cluster, String spot) {
        this.planet = planet;
        this.floor = floor;
        this.cluster = cluster;
        this.spot = spot;
    }

    /**
     * <pre>
     *     Planet 갱신
     *     locate 파싱 전 planet 정보만 갱신
     * </pre>
     * @param planet 갱신할 플래닛
     * @since 1.0
     * @author hyunjcho
     */
    public void updatePlanet(Planet planet) {
        this.planet = planet;
    }

    /**
     * locate 갱신
     * @param planet 플래닛
     * @param floor 개포의 경우 층수
     * @param cluster 서초의 경우 클러스터
     * @param spot 자리
     * @since 1.0
     * @author hyunjcho
     */
    public void updateLocate(Planet planet, int floor, int cluster, String spot) {
        this.planet = planet;
        this.floor = floor;
        this.cluster = cluster;
        this.spot = spot;
    }

    /**
     * <pre>
     *     42api 아이맥 로그인/아웃(location) 정보 파싱
     *     location이 null일 경우 외출/퇴근, 0~6 및 x의 경우 개포 - 층, 7~10의 경우 서초 - 클러스터로 파싱
     * </pre>
     * @param location 42api location 정보
     * @return 파싱된 위치 정보
     * @since 1.0
     * @author huynjcho
     */
    public static Locate parseLocate(String location) {
        if (location == null)
            return new Locate(null, 0, 0, null);
        int i = location.charAt(1) - '0';
        if ((i >= 1 && location.charAt(2) != '0') && i <= 6 || location.charAt(1) == 'x') {
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
