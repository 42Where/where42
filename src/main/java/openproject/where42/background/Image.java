package openproject.where42.background;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "image")
public class Image {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	String name;
	String img;
	String location;
	boolean active;
}
