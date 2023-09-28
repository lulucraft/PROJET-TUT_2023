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

	UserShareRight removeRightToUserShareRight(UserShareRight usr, Right right);

	Collection<UserShareRight> getUserShareRightsFromUser(User user);

	UserShareRight getUserShareRightFromUserFileOwner(User user, User fileOwner);

	boolean hasShareRights(User user);

	void deleteUserShareRight(UserShareRight userShareRight);

	Collection<UserShareRight> getUserShareRightsFromUserSharer(User user);

//	Collection<UserShareRight> getUserShareRightFromUserAndUserOwner(User user, User fileOwner);
}
