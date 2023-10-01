package fr.nepta.cloud.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@NoArgsConstructor @AllArgsConstructor @Getter @Setter
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

	@JsonIgnore
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
	@ManyToOne
	private Offer offer;

	@OneToMany(fetch = FetchType.LAZY)
	private Collection<Order> orders;

	@ManyToMany(fetch = FetchType.EAGER)
	private Collection<Role> roles = new ArrayList<>();

	@ManyToMany(fetch = FetchType.LAZY)
	private Collection<File> files = new ArrayList<>();

//	@EqualsAndHashCode.Exclude
	@JsonBackReference
	@ManyToMany(fetch = FetchType.EAGER)
	private Set<UserShareRight> userShareRights = new HashSet<>();

	@Override
	public User clone() throws CloneNotSupportedException {
		return (User) super.clone();
	}

//	@Override
//	public boolean equals(Object obj) {
//		return super.equals(obj);
//	}

}
