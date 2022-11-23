package openproject.where42.member.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FlashMember {
	@Id
	String name;
	Locate locate;
	int inOrOut;
	@Temporal(TemporalType.TIMESTAMP)
	Date wasTime;

	public FlashMember(String name, Locate locate, int inOrOut) {
		this.name = name;
		this.locate = locate;
		this.inOrOut = inOrOut;
		this.wasTime = new Date();
	}

	public void updateStatus(Locate locate, int inOrOut) {
		this.locate = locate;
		this.inOrOut = inOrOut;
	}

	public void updateTime() {
		Date isTime = new Date();
	}

	public Long timeDiff() {
		Date now = new Date();
		return ((now.getTime() - wasTime.getTime()) / 60000);
	}
}
