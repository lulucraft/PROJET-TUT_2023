package fr.nepta.cloud.service.impl;

import java.util.Collection;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.nepta.cloud.model.Right;
import fr.nepta.cloud.model.User;
import fr.nepta.cloud.model.UserShareRight;
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

	@Override
	public UserShareRight saveUserShareRight(UserShareRight usr) {
		log.info("Saving userShareRight '{}' in the database", usr.getId());
		return usrRepo.save(usr);//User user, R
	}

	@Override
	public Collection<UserShareRight> getUsersSharedRights() {
		log.info("Fetching all user_share_right");
		return usrRepo.findAll();
	}

	@Override
	public UserShareRight getUserShareRight(long id) {
		log.info("Fetching user_share_right '{}'", id);
		return usrRepo.findById(id).get();
	}

	@Override
	public Collection<UserShareRight> getUserShareRightFromUser(User user) {
		log.info("Fetching user_share_right from user '{}'", user.getId());
		return usrRepo.findByUserId(user.getId());
	}

	@Override
	public UserShareRight getUserShareRightFromUserFileOwner(User user, User fileOwner) {
		Collection<UserShareRight> usrs = this.getUserShareRightFromUser(user);
		for (UserShareRight usr : usrs) {
			for (UserShareRight ownerUsr : fileOwner.getUserShareRights()) {
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
	public UserShareRight removeRightToUserShareRight(UserShareRight usr, Right right) {
		log.info("Removing right '{}' from shared user '{}'", right.getId(), usr.getId());
		usr.getRights().remove(right);
		return usrRepo.save(usr);
	}

}
