package fr.nepta.cloud.repository;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import fr.nepta.cloud.model.UserShareRight;

public interface UserShareRightRepo extends JpaRepository<UserShareRight, Long> {

	Collection<UserShareRight> findByUserId(long id);

	@Query("SELECT usr FROM user u JOIN u.userShareRights usr ON u.id = :sharerId")
	Collection<UserShareRight> findByUserSharerId(long sharerId);
}
