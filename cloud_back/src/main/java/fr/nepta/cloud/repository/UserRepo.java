package fr.nepta.cloud.repository;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import fr.nepta.cloud.model.User;

public interface UserRepo extends JpaRepository<User, Long> {

	User findByUsername(String username);

	User findByEmail(String email);

	User findByUserShareRightsId(long id);

	@Query("SELECT u FROM user u JOIN u.userShareRights usr ON usr.user.id = :userId")
	Collection<User> findUsersSharerByUserId(long userId);

//	@Query("DELETE FROM user u JOIN u.userShareRights usr ON usr.id = :userShareRightId WHERE u.id = :userSharerId")
//	void removeUserShareRightFromUser(long userSharerId, long userShareRightId);

//	@Query("SELECT o FROM user u JOIN u.orders o WHERE u.id = :userId AND o.archived = false")
//	List<Order> findOrdersNotArchived(long userId);

//	List<Order> findOrdersWhereOrderNotArchived(User user);

}
