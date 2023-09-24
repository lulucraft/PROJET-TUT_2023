package fr.nepta.cloud.model;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
@Entity(name = "order")
@Table(name = "`order`")
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "paypal_id", unique = true)
	private String paypalId;

	@Column(name = "order_date", nullable = false)
	@DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
	private Date date;

//	@Column(name = "offer_id")
	@ManyToOne(fetch = FetchType.EAGER)
	private Offer offer;

	@Column(name = "quantity", nullable = false)
	private int quantity;

	@Column(name = "archived", nullable = false, columnDefinition="tinyint(1) default 0")
	private boolean archived;

//	@ManyToOne(targetEntity = User.class)
////	@JoinColumn(name = "user_id")
//	private User userRef;
}
