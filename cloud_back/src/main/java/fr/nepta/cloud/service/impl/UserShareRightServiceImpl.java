package fr.nepta.cloud.service.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.nepta.cloud.model.Right;
import fr.nepta.cloud.model.User;
import fr.nepta.cloud.model.UserShareRight;
import fr.nepta.cloud.repository.RightRepo;
import fr.nepta.cloud.repository.UserRepo;
import fr.nepta.cloud.repository.UserShareRightRepo;
import fr.nepta.cloud.service.UserShareRightService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor @Slf4j
@Transactional
public class UserShareRightServiceImpl implements UserShareRightService {

	@Autowired
	private final UserShareRightRepo usrRepo;
	@Autowired
	private final RightRepo rgtRepo;
	@Autowired
	private final UserRepo usRepo;

	@Override
	public UserShareRight saveUserShareRight(UserShareRight usr) {
		log.info("Saving userShareRight '{}' in the database", usr.getId());
		return usrRepo.save(usr);//User user, R
	}

//	@Override
//	public Collection<UserShareRight> getUsersSharedRights() {
//		log.info("Fetching all user_share_right");
//		return usrRepo.findAll();
//	}

	@Override
	public Optional<UserShareRight> getUserShareRight(long id) {
		log.info("Fetching user_share_right '{}'", id);
		return usrRepo.findById(id);
	}

	@Override
	public Collection<UserShareRight> getUserShareRightsFromUser(User user) {
		log.info("Fetching user_share_right from user '{}'", user.getId());
		return usrRepo.findByUserId(user.getId());
	}

	@Override
	public UserShareRight getUserShareRightFromUserAndUserSharer(User user, User userSharer) {
		log.info("Fetching user_share_right from user '{}' and user sharer '{}'", user.getId(), userSharer.getId());
		Collection<UserShareRight> usrs = this.getUserShareRightsFromUser(user);
		for (UserShareRight usr : usrs) {
			for (UserShareRight ownerUsr : this.getUserShareRightsFromUserSharer(userSharer)) {
				if (usr.getId() == ownerUsr.getId()) {
					return usr;
				}
			}
		}

		return null;
	}

	@Override
	public UserShareRight shareRightsToUser(User user, User userToShare, Set<Right> userRights) {
		log.info("Adding user '{}' to shared users of ", userToShare.getId());
		if (getUserShareRightFromUserAndUserSharer(userToShare, user) != null) {
			log.error("Utilisateur déjà partagé");
			return null;
		}

		if (userRights == null) userRights = new HashSet<>();
		Right showRight = rgtRepo.findByName("Afficher");
		if (!userRights.contains(showRight)) {
			// Add "Afficher" right
			userRights.add(showRight);
		}

		UserShareRight usr = new UserShareRight(null, userToShare, userRights);
		Set<UserShareRight> userShareRights = user.getUserShareRights();
		userShareRights.add(usr);
		user.setUserShareRights(userShareRights);

		return usrRepo.save(usr);
	}

	@Override
	public UserShareRight addRightToUserShareRight(UserShareRight usr, Right right) {
		log.info("Adding right '{}' to shared user '{}'", right.getId(), usr.getId());
		usr.getRights().add(right);
		return usrRepo.save(usr);
	}

	@Override
	public UserShareRight removeRightOfUserShareRight(UserShareRight usr, Right right) {
		log.info("Removing right '{}' from shared user '{}'", right.getId(), usr.getId());
		usr.getRights().remove(right);
		return usrRepo.save(usr);
	}

	@Override
	public boolean hasShareRights(User user) {
		return this.getUserShareRightsFromUser(user).size() > 0;
	}

	@Override
	public void deleteUserShareRight(User userSharer, UserShareRight userShareRight) {
		log.info("Deleting rights of user '{}'", userShareRight.getUser().getId());
//		getUserShareRightsFromUser(userSharer).remove(userShareRight);
		// Remove the sharing from user share
		userSharer.getUserShareRights().remove(userShareRight);
		// Save removing
		usRepo.save(userSharer);
//		usRepo.removeUserShareRightFromUser(userSharer.getId(), userShareRight.getId());
		// Remove all associated rights
//		for (Right right : userShareRight.getRights()) {
//			usrRepo.removeRight(userShareRight.getId(), right);
//		}
		// Save removing
		userShareRight = usrRepo.save(userShareRight);
//		usrRepo.removeAllRightsOfUserShareRight(userShareRight);
		usrRepo.delete(userShareRight);
	}

	@Override
	public Set<UserShareRight> getUserShareRightsFromUserSharer(User user) {
		return usrRepo.findByUserSharerId(user.getId());
	}

}
