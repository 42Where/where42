package openproject.where42.iamge;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "iamge")
public class Image {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	String name;
	String img;
	String active;
}
