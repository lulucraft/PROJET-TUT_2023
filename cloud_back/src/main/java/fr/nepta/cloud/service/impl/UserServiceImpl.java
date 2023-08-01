package fr.nepta.cloud.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.nepta.cloud.model.File;
import fr.nepta.cloud.model.Offer;
import fr.nepta.cloud.model.Order;
import fr.nepta.cloud.model.Role;
import fr.nepta.cloud.model.User;
import fr.nepta.cloud.repository.RoleRepo;
import fr.nepta.cloud.repository.UserRepo;
import fr.nepta.cloud.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor @Slf4j
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService {

	private final UserRepo userRepo;
	private final RoleRepo roleRepo;
	private final PasswordEncoder passEncoder;

	@Override
	public User saveUser(User user) {
		log.info("Saving new user to the database");
		user.setPassword(passEncoder.encode(user.getPassword()));
		return userRepo.save(user);
	}

	@Override
	public void addRoleToUser(User user, Role role) {
		user.getRoles().add(role);
		userRepo.save(user);
		log.info("Role '{}' added to user '{}'", role.getName(), user.getUsername());
	}

	@Override
	public void addRoleToUser(String username, String roleName) {
		addRoleToUser(userRepo.findByUsername(username), roleRepo.findByName(roleName));
	}

	@Override
	public User getUser(String username) {
		log.info("Fetching user '{}' from database", username);
		return userRepo.findByUsername(username);
	}

	@Override
	public List<User> getUsers() {
		log.info("Fetching all users");

		List<User> users = new ArrayList<>();

		// new ArrayList<User>(... to clone users list without modify it -> when u.setPassword(null)
		for (User u : userRepo.findAll()) {
			try {
				// Clone to avoid user password change
				User user = u.clone();
				user.setPassword(null);
				users.add(user);
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}

		return users;
	}

	@Override
	public User getUser(long userId) throws Exception {
		log.info("Fetching user {}", userId);

		Optional<User> optUser = userRepo.findById(userId);
		if (!optUser.isPresent()) {
			log.error("User '{}' not found in the database", userId);
			throw new Exception("User not found in the database");
		}

		// Clone to avoid user password change
		User u = optUser.get().clone();

		if (u != null) u.setPassword(null);

		return u;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User u = userRepo.findByUsername(username);

		if (u == null) {
			log.error("User '{}' not found in the database", username);
			throw new UsernameNotFoundException("User not found in the database");
		}

		Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
		u.getRoles().forEach(r -> {
			authorities.add(new SimpleGrantedAuthority(r.getName()));
		});

		return new org.springframework.security.core.userdetails.User(u.getUsername(), u.getPassword(), authorities);
	}

	@Override
	public void setDarkMode(User user, boolean darkModeEnabled) throws Exception {
		if (user == null) {
			log.error("User is null");
			throw new Exception("User is null");
		}

		user.setDarkModeEnabled(darkModeEnabled);
		log.info("Dark mode changed to '{}' for user '{}'", darkModeEnabled, user.getUsername());
	}

	@Override
	public void editUser(User user) throws Exception {
		if (user == null) {
			log.error("User is null");
			throw new Exception("User is null");
		}

		User u = userRepo.findById(user.getId()).get();
		if (u == null) {
			log.error("User '{}' not found in the database", user.getId());
			throw new Exception("User not found in database");
		}

		// Set only the modifiable fields
		if (user.getUsername() != null) u.setUsername(user.getUsername());
		if (user.getFirstname() != null) u.setFirstname(user.getFirstname());
		if (user.getLastname() != null) u.setLastname(user.getLastname());
		if (user.getEmail() != null) u.setEmail(user.getEmail());
//		if (user.getCongesNbr() >= 0) u.setCongesNbr(user.getCongesNbr());
		u.setAccountActive(user.isAccountActive());

		log.info("User '{}' updated", u.getId());
	}

	@Override
	public void addFileToUser(User user, File file) throws Exception {
		if (user == null) {
			log.error("User is null");
			throw new Exception("User is null");
		}

		Collection<File> files = user.getFiles();
		files.add(file);
		user.setFiles(files);
//		userRepo.save(user);

		log.info("Saving '{}' on the database", user.getId());
	}

	@Override
	public User getUserFromEmail(String email) throws Exception {
		log.info("Fetching user '{}' from the database", email);
		return userRepo.findByEmail(email);
	}

	@Override
	public void addOrderToUser(User user, Order order) {
		user.getOrders().add(order);
		userRepo.save(user);

		log.info("Adding order '{}' to user '{}'", order.getId(), user.getId());
	}

	@Override
	public void setOffer(User user, Offer offer) {
		user.setOffer(offer);

		log.info("Set offer '{}' to user '{}'", offer.getId(), user.getId());
	}

}
