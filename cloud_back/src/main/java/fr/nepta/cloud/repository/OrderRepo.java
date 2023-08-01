package fr.nepta.cloud.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.nepta.cloud.model.Order;

public interface OrderRepo extends JpaRepository<Order, Long> {

	Order findByPaypalId(String paypalId);

}
