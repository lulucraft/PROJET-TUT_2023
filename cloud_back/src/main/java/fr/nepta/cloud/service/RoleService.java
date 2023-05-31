package fr.nepta.cloud.service;

import fr.nepta.cloud.model.Role;

public interface RoleService {

	Role saveRole(Role role);

	Role getRole(String roleName);

}
