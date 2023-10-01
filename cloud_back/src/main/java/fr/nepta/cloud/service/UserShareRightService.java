package fr.nepta.cloud.service;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import fr.nepta.cloud.model.Right;
import fr.nepta.cloud.model.User;
import fr.nepta.cloud.model.UserShareRight;

public interface UserShareRightService {

	UserShareRight saveUserShareRight(UserShareRight usr);

//	Collection<UserShareRight> getUsersSharedRights();

	Optional<UserShareRight> getUserShareRight(long id);

	UserShareRight shareRightsToUser(User user, User userToShare, Set<Right> userShareRights);

	UserShareRight addRightToUserShareRight(UserShareRight usr, Right right);

	UserShareRight removeRightOfUserShareRight(UserShareRight usr, Right right);

//	void removeAllRightsOfUserShareRight(UserShareRight usr);

	Collection<UserShareRight> getUserShareRightsFromUser(User user);

	UserShareRight getUserShareRightFromUserAndUserSharer(User user, User fileOwner);

	boolean hasShareRights(User user);

	void deleteUserShareRight(User user, UserShareRight userShareRight);

	Set<UserShareRight> getUserShareRightsFromUserSharer(User user);

//	Collection<UserShareRight> getUserShareRightFromUserAndUserOwner(User user, User fileOwner);
}
