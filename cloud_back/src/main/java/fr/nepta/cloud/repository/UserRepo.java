package fr.nepta.cloud.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.nepta.cloud.model.User;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {

	User findByUsername(String username);

}
