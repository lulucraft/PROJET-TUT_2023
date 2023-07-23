package fr.nepta.cloud.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.nepta.cloud.model.Role;

public interface RoleRepo extends JpaRepository<Role, Long> {

	Role findByName(String roleName);

}
