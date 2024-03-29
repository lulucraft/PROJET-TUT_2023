package fr.nepta.cloud.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name = "offer")
public class Offer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name")
	private String name;

	@Column(name = "price")
	private long price;

	// In months
	@Column(name = "duration")
	private int duration;

	@ElementCollection//(targetClass = String.class, fetch = FetchType.EAGER)
	@CollectionTable(name = "advantages", joinColumns = @JoinColumn(name = "id"))
	@Column(name = "label", nullable = false)
	private Set<String> advantages;

	@ElementCollection//(targetClass = String.class, fetch = FetchType.EAGER)
	@CollectionTable(name = "disadvantages", joinColumns = @JoinColumn(name = "id"))
	@Column(name = "label", nullable = false)
	private Set<String> disadvantages = new HashSet<>();

}
