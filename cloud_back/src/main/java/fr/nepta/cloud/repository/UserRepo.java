package fr.nepta.cloud.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import fr.nepta.cloud.model.Order;
import fr.nepta.cloud.model.User;

public interface UserRepo extends JpaRepository<User, Long> {

	User findByUsername(String username);

	User findByEmail(String email);

//	@Query("SELECT o FROM user u JOIN u.orders o WHERE u.id = :userId AND o.archived = false")
//	List<Order> findOrdersNotArchived(long userId);

//	List<Order> findOrdersWhereOrderNotArchived(User user);

}
