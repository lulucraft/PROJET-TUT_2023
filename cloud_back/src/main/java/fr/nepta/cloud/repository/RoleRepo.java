package fr.nepta.cloud.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.nepta.cloud.model.Role;

@Repository
public interface RoleRepo extends JpaRepository<Role, Long> {

	Role findByName(String roleName);

}
