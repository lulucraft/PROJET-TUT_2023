package fr.nepta.cloud.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor @Getter
@Entity
@Table(name = "file")
public class File {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name")
	private String name;

	@Column(name = "creationdate", nullable = false)
	@DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
	private Date creationDate;

	@Column(name = "modificationdate", nullable = false)
	@DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
	private Date startDate;

}
