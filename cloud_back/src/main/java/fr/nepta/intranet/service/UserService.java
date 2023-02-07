package fr.nepta.intranet.service;

import java.util.List;

import fr.nepta.intranet.model.Conge;
import fr.nepta.intranet.model.Role;
import fr.nepta.intranet.model.User;

public interface UserService {

	User saveUser(User user);

//	Role saveRole(Role role);

	void addRoleToUser(User user, Role role);

	void addRoleToUser(String username, String roleName);

	User getUser(String username);

	List<User> getUsers();

	User getUser(long userId) throws Exception;

	void addCongeToUser(User user, Conge conge);

	void deleteCongeFromUser(User user, long congeId) throws Exception;

	void setDarkMode(User user, boolean darkModeEnabled) throws Exception;

	void editUser(User user) throws Exception;

//	List<Conge> getConges();

}
