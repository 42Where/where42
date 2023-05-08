package openproject.where42.member.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Getter
@Entity
@SequenceGenerator(
		name = "LOCATIONS_SEQ_GENERATOR",
		sequenceName = "LOCATIONS_SEQ",
		initialValue = 1, allocationSize = 1
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Locations {

	@Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LOCATIONS_SEQ")
	private Long id;
	private String name;
	private Locate locate;
	@Temporal(TemporalType.TIMESTAMP)
	Date setTime;

	public Locations(String name, Locate locate, Date time) {
		this.name = name;
		this.locate = locate;
		this.setTime = time;
	}
}
