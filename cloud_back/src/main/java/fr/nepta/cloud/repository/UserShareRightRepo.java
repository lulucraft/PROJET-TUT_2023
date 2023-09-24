package fr.nepta.cloud.repository;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.nepta.cloud.model.UserShareRight;

public interface UserShareRightRepo extends JpaRepository<UserShareRight, Long> {

	Collection<UserShareRight> findByUserId(long id);
}
