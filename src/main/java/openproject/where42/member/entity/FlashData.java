package openproject.where42.member.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import openproject.where42.api.dto.Image;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FlashData {
	@Id
	private String name;
	private Locate locate = new Locate(null, 0, 0, null);
	private int inOrOut;

	private String location;

	@Embedded
	private Image image;
	@Temporal(TemporalType.TIMESTAMP)
	private Date updateTime;

	public FlashData(String name, Image image, String location) {
		this.name = name;
		this.image = image;
		this.location = location;
		this.updateTime = new Date();
	}

	public void updateLocation(String location) {
		this.location = location;
		this.updateTime = new Date();
	}
	public void updateStatus(Locate locate, int inOrOut) {
		this.locate = locate;
		this.inOrOut = inOrOut;
		this.location = null;
		this.updateTime = new Date();
	}

	public Long timeDiff() {
		Date now = new Date();
		return ((now.getTime() - updateTime.getTime()) / 60000);
	}
}
