package fr.nepta.cloud.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.nepta.cloud.model.Role;
import fr.nepta.cloud.repository.RoleRepo;
import fr.nepta.cloud.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor @Slf4j
@Transactional
public class RoleServiceImpl implements RoleService {

	private final RoleRepo roleRepo;

	@Override
	public Role saveRole(Role role) {
		log.info("Saving new role to the database");
		return roleRepo.save(role);
	}

	@Override
	public Role getRole(String roleName) {
		log.info("Fetching role '{}'", roleName);
		return roleRepo.findByName(roleName);
	}

}
