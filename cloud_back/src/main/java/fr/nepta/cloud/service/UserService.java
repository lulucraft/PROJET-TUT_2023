package fr.nepta.cloud.service;

import java.util.Collection;
import java.util.List;

import fr.nepta.cloud.model.File;
import fr.nepta.cloud.model.Offer;
import fr.nepta.cloud.model.Order;
import fr.nepta.cloud.model.Role;
import fr.nepta.cloud.model.User;
import fr.nepta.cloud.model.UserShareRight;

public interface UserService {

	User saveUser(User user);

	void addRoleToUser(User user, Role role);

	void addRoleToUser(String username, String roleName);

	User getUser(String username);

	List<User> getUsers();

	User getUser(long userId) throws Exception;

	void setDarkMode(User user, boolean darkModeEnabled) throws Exception;

	void editUser(User user) throws Exception;

	void addFileToUser(User user, File file) throws Exception;

	User getUserFromEmail(String email) throws Exception;

	void addOrderToUser(User user, Order order) throws Exception;

	void setOffer(User user, Offer offer);

	void archiveUserFile(User user, long fileId) throws Exception;

	User getUserFromUserShareRight(UserShareRight usr);

	Collection<User> getUsersSharer(User user);

//	void removeFileFromUser(User user, long fileId) throws Exception;

//	List<Order> getOrdersNotArchivedByUser(User user);

}
