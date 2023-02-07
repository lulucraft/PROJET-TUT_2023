package fr.nepta.intranet.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor @Getter
@Entity
@Table(name = "user")
public class User implements Cloneable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "firstname")
	private String firstname;

	@Column(name = "lastname")
	private String lastname;

	@Column(name = "email")
	private String email;

	@Column(name = "username")
	private String username;

	@Column(name = "password")
	private String password;

	@Column(name = "creationdate", nullable = false)
	@DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
	private Date creationDate;

	@Column(name = "congesnbr", nullable = false)
	private double congesNbr;

	@Column(name = "darkmode", nullable = false, columnDefinition="tinyint(1) default 1")
	private boolean darkModeEnabled;

	@Column(name = "account_active", nullable = false, columnDefinition="tinyint(1) default 1")
	private boolean accountActive;

	//	@Column(name = "access_token")
	//	private String accessToken;
	//
	//	@Column(name = "refresh_token")
	//	private String refreshToken;

	@ManyToMany(fetch = FetchType.EAGER)
	private Collection<Role> roles = new ArrayList<>();

	@ManyToMany(fetch = FetchType.LAZY)
	private Collection<Conge> conges = new ArrayList<>();

	@Override
	public User clone() throws CloneNotSupportedException {
		return (User) super.clone();
	}
}
