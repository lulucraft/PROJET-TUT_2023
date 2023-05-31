package fr.nepta.cloud.service;

import java.util.List;

import fr.nepta.cloud.model.Role;
import fr.nepta.cloud.model.User;

public interface UserService {

	User saveUser(User user);

//	Role saveRole(Role role);

	void addRoleToUser(User user, Role role);

	void addRoleToUser(String username, String roleName);

	User getUser(String username);

	List<User> getUsers();

	User getUser(long userId) throws Exception;

//	void addCongeToUser(User user, Conge conge);

	void setDarkMode(User user, boolean darkModeEnabled) throws Exception;

	void editUser(User user) throws Exception;

}
