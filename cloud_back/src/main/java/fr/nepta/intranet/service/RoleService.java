package fr.nepta.intranet.service;

import fr.nepta.intranet.model.Role;

public interface RoleService {

	Role saveRole(Role role);

	Role getRole(String roleName);

}
