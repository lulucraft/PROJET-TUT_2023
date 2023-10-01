package fr.nepta.cloud.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
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
import fr.nepta.cloud.model.UserShareRight;
import fr.nepta.cloud.repository.FileRepo;
import fr.nepta.cloud.repository.OrderRepo;
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
	private final OrderRepo orderRepo;
	private final FileRepo fileRepo;
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
		if (user.getOffer() != null) u.setOffer(user.getOffer());
		u.setAccountActive(user.isAccountActive());

		log.info("User '{}' updated", u.getId());
	}

	@Override
	public void addFileToUser(User user, File file) throws Exception {
		if (user == null) {
			log.error("User is null");
			throw new Exception("User is null");
		}

		if (user.getOffer() == null) {
			log.error("L'utilisateur '{}' n'a pas souscrit d'offre mais tente d'ajouter des fichiers", user.getId());
			throw new IllegalStateException("Vous n'avez souscrit à aucune offre");
		}

		if (checkUserOffer(user) == true) {
			// Révocation de l'offre de l'utilisateur si offre expirée
			user.setOffer(null);
			// Reset des partages
			user.setUserShareRights(null);
			log.error("Révocation de l'offre de l'utilisateur '{}' arrivée à expiration", user.getId());
			return;
		}

		Collection<File> files = user.getFiles();
		files.add(file);
		user.setFiles(files);
		//userRepo.save(user);

		log.info("Saving user '{}' in the database", user.getId());
	}

	private boolean checkUserOffer(User user) {
		Offer o = user.getOffer();
		if (o == null) {
			log.error("L'utilisateur '{}' n'a pas souscrit d'offre", user.getId());
			return false;
		}

		//List<Order> orders = orderRepo.findByUser(user);
		//Collection<Order> orders = user.getOrders();

		//		List<Order> orders = orderRepo.findAllNotArchivedByUserId(user.getId());
		//		if (orders.size() == 0) {
		//			log.error("L'utilisateur '{}' n'a aucune commande liée à son offre", user.getId());
		//			return false;
		//		}

		Order or = orderRepo.findLastNotArchivedByUserId(user.getId());
		if (or == null) {
			log.error("L'utilisateur '{}' n'a aucune commande liée à son offre", user.getId());
			return false;
		}
		int offerMonthsDuration = o.getDuration();

		//		for (Order or : orders) {
		Date orderDate = or.getDate();
		System.err.println(orderDate);
		Calendar expiration = Calendar.getInstance();
		expiration.setTime(orderDate);
		// Durée * quantité souscrite
		expiration.add(Calendar.MONTH, offerMonthsDuration * or.getQuantity());
		//LocalDate.now().plusMonths(offerMonthsDuration * or.getQuantity())
		//	System.err.println(new Date().toString());
		System.err.println(expiration.getTime().toString());
		// Offre expirée si date de fin de l'offre après la date d'aujourd'hui
		if (expiration.getTime().before(new Date())) {
			log.error("L'utilisateur '{}' n'a aucune commande non expirée liée à son offre", user.getId());
			// Révocation de l'offre de l'utilisateur si offre expirée
			//user.setOffer(null);
			// Archivage de la commande comme pas archivée et offre expirée
			or.setArchived(true);
			orderRepo.save(or);
			log.error("Commande '{}' expirée, archivée pour l'utilisateur '{}'", or.getId(), user.getId());
			return true;
		}

		log.info("The user '{}' has offer '{}'", user.getId(), or.getOffer().getId());
		//	}

		//	if (o.isArchived() && o.getDate()) {
		//	}

		return false;
	}

	//	@Override
	//	public List<Order> getOrdersNotArchivedByUser(User user) {
	//		return userRepo.findOrdersWhereOrderNotArchived(user);
	//	}

	@Override
	public User getUserFromEmail(String email) throws Exception {
		log.info("Fetching user '{}' from the database", email);

		User user = userRepo.findByEmail(email);
		if (user == null) {
			log.error("User not found in the database");
			throw new Exception("User not found in the database");
		}

		return user;
	}

	@Override
	public void addOrderToUser(User user, Order order) throws Exception {
		if (user == null) {
			log.error("User is null");
			throw new Exception("User is null");
		}

		user.getOrders().add(order);
		userRepo.save(user);

		log.info("Adding order '{}' to user '{}'", order.getId(), user.getId());
	}

	@Override
	public void setOffer(User user, Offer offer) {
		user.setOffer(offer);
		//userRepo.save(user);

		log.info("Set offer '{}' to user '{}'", offer.getId(), user.getId());
	}

//	@Override
//	public void removeFileFromUser(User user, long fileId) throws Exception {
//		Optional<File> optFile = fileRepo.findById(fileId);
//		if (!optFile.isPresent()) {
//			log.error("File '{}' not found in the database", fileId);
//			throw new Exception("File not found in the database");
//		}
//
//		File file = optFile.get();
//		user.getFiles().remove(file);
//
//		log.info("File '{}' removed from files list of user '{}'", file.getId(), user.getId());
//	}

	@Override
	public void archiveUserFile(User user, long fileId) throws Exception {
		Optional<File> optFile = fileRepo.findById(fileId);
		if (!optFile.isPresent()) {
			log.error("File '{}' not found in the database", fileId);
			throw new Exception("File not found in the database");
		}

		File file = optFile.get();
//		for (File f : user.getFiles()) {
//		}
		file.setArchived(true);

		log.info("File '{}' archived for user '{}'", file.getId(), user.getId());
	}

	@Override
	public User getUserFromUserShareRight(UserShareRight usr) {
		return this.userRepo.findByUserShareRightsId(usr.getId());
	}

	@Override
	public Collection<User> getUsersSharer(User user) {
		Collection<User> users = new ArrayList<>();
		for (User u : this.userRepo.findUsersSharerByUserId(user.getId())) {
			User use = new User();
			use.setId(u.getId());
			use.setEmail(u.getEmail());
			users.add(use);
		}
		return users;
	}

}
