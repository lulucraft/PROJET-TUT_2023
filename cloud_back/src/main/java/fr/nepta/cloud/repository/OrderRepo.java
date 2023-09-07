package fr.nepta.cloud.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import fr.nepta.cloud.model.Order;

public interface OrderRepo extends JpaRepository<Order, Long> {

	Order findByPaypalId(String paypalId);

	@Query("SELECT o FROM user u JOIN u.orders o WHERE u.id = :userId AND o.archived = false")
	List<Order> findAllNotArchivedByUserId(long userId);
	
	@Query("SELECT o FROM user u JOIN u.orders o WHERE u.id = :userId AND o.archived = false ORDER BY o.date DESC")
	Order findLastNotArchivedByUserId(long userId);

//	List<Order> findAllNotArchivedByUser(long userId);

//	Order findFirstNotArchivedByUserOrderByDateDesc(User user);

//	List<Order> findByUser(User user);
//
//	List<Order> findByUserAndNotArchived(User user);

}
