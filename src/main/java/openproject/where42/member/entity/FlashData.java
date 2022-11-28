package openproject.where42.member.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import openproject.where42.api.Define;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FlashData {
	@Id
	private String name;
	private String img;
	private int inOrOut;
	@Embedded
	private Locate locate = new Locate(null, 0, 0, null);
	private String location;
	@Temporal(TemporalType.TIMESTAMP)
	private Date updateTime;

	public FlashData(String name, String img, String location) {
		this.name = name;
		this.img = img;
		this.location = location;
		this.updateTime = new Date();
	}

	public void updateLocation(String location) {
		this.location = location;
		this.updateTime = new Date();
	}

	public void parseStatus(Locate locate) {
		this.locate = locate;
		if (locate.getPlanet() != null)
			this.inOrOut = Define.IN;
		else
			this.inOrOut = Define.NONE;
		this.location = Define.PARSED;
	}

	public Long timeDiff() {
		Date now = new Date();
		return ((now.getTime() - updateTime.getTime()) / 60000);
	}
}
