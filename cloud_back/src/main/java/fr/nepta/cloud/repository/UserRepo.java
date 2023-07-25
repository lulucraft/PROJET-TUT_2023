package fr.nepta.cloud.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.nepta.cloud.model.User;

public interface UserRepo extends JpaRepository<User, Long> {

	User findByUsername(String username);

	User findByEmail(String email);

}
