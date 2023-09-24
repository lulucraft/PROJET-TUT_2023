package fr.nepta.cloud.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor @Getter
@Entity(name = "user")
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

	@Column(name = "darkmode", nullable = false, columnDefinition="tinyint(1) default 1")
	private boolean darkModeEnabled;

	@Column(name = "account_active", nullable = false, columnDefinition="tinyint(1) default 1")
	private boolean accountActive;

	//	@Column(name = "access_token")
	//	private String accessToken;
	//
	//	@Column(name = "refresh_token")
	//	private String refreshToken;

	//	@Column(name = "offer_id")
	@OneToOne
	private Offer offer;

	@OneToMany(fetch = FetchType.LAZY)
	private Collection<Order> orders;

	@ManyToMany(fetch = FetchType.EAGER)
	private Collection<Role> roles = new ArrayList<>();

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
			name = "user_files",
			joinColumns = @JoinColumn(name = "user_id"),
			inverseJoinColumns = @JoinColumn(name = "files_id")
			)
	private Collection<File> files = new ArrayList<>();

	@ManyToMany(fetch = FetchType.LAZY)
	private Set<UserShareRight> userShareRights = new HashSet<>();

	@Override
	public User clone() throws CloneNotSupportedException {
		return (User) super.clone();
	}
}
