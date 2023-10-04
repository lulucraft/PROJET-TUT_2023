package fr.nepta.cloud.api.user;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import fr.nepta.cloud.SFTPConfig.UploadFileData;
import fr.nepta.cloud.SFTPConfig.UploadGateway;
import fr.nepta.cloud.model.File;
import fr.nepta.cloud.model.Offer;
import fr.nepta.cloud.model.Order;
import fr.nepta.cloud.model.Right;
import fr.nepta.cloud.model.User;
import fr.nepta.cloud.model.UserShareRight;
import fr.nepta.cloud.service.FileService;
import fr.nepta.cloud.service.OfferService;
import fr.nepta.cloud.service.OrderService;
import fr.nepta.cloud.service.PayPalService;
import fr.nepta.cloud.service.RightService;
import fr.nepta.cloud.service.UserService;
import fr.nepta.cloud.service.UserShareRightService;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor @Slf4j
//@CrossOrigin(origins = "https://intranet.tracroute.lan/", maxAge = 3600)
//"192.168.10.2","192.168.10.3", "192.168.10.4"
@CrossOrigin(origins = {"https://tuxit.site/", "51.79.109.241"}, maxAge = 4800, allowCredentials = "false", methods = { RequestMethod.GET, RequestMethod.OPTIONS, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE })
@RestController
@RequestMapping("api/user/")
public class UserController {

	@Autowired
	private final UserService us;
	@Autowired
	private final FileService fs;
	@Autowired
	private final OfferService os;
	@Autowired
	private final OrderService ors;
	@Autowired
	private final RightService rgts;
	@Autowired
	private final UserShareRightService usrs;

	@Autowired
	private final UploadGateway sftpUploadGateway;

	//	@GetMapping(value = "users")
	//	public String getUsers() {
	//		return us.getUsers().toString();
	//	}

	@RolesAllowed({"USER","ADMIN"})
	@GetMapping(value = "files")
	public Collection<File> getFiles() {//@RequestParam String username
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return us.getUser(auth.getName()).getFiles().stream().filter(f -> f.isArchived() == false).toList();
	}

	@RolesAllowed({"USER","ADMIN"})
	@GetMapping(value = "sharedfiles")
	public Collection<File> getSharedFiles(@RequestParam(name = "user_sharer_id") long userSharerId) throws Exception {//@RequestParam String username
//		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//		User user = us.getUser(auth.getName());
		User userOwner = us.getUser(userSharerId);
		// Get all shared
//		Collection<UserShareRight> usrS = usrs.getUserShareRightFromUserAndUserOwner(user, userOwner);

		Collection<File> files = new ArrayList<>();
//		for (UserShareRight usr : usrS) {
//			User userOwner = us.getUserFromUserShareRight(usr);
		Collection<File> userOwnerFiles = userOwner.getFiles().stream().filter(f -> f.isArchived() == false).toList();
		if (userOwnerFiles != null && userOwnerFiles.size() > 0) {
			// Add all files of user who has shared files
			files.addAll(userOwnerFiles);
			System.err.println(userOwnerFiles.size());
		} else {
			log.error("Error: no files found for user '{}'", userOwner.getId());
		}
//		}

		return files;
	}

//	@RolesAllowed({"USER","ADMIN"})
//	@PutMapping(value = "file")
//	public File addFile(@RequestBody File file) {//@RequestBody File file
//		log.info("Saving file {} on the database", file.getId());
//		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//		File f = fs.saveFile(new File(null, file.getName(), new Date(), null, file.getSize(), file.getHash(), false));
//		try {
//			us.addFileToUser(us.getUser(auth.getName()), f);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return f;
//	}

//	@RolesAllowed({"USER","ADMIN"})
//	@PutMapping(value = "sharedfile")
//	public File addSharedFile(@RequestBody File file, @RequestParam(name = "user_sharer_id") long userSharerId) throws Exception {
//		log.info("Saving shared file {} on the database", file.getId());
//		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//		User user = us.getUser(auth.getName());
//		User userSharer = us.getUser(userSharerId);
//		if (user == userSharer) {
//			throw new IllegalAccessError("Vous ne pouvez pas partager à vous même");
//		}
//
//		Right addRight = rgts.getRight("Ajouter");
//		if (!usrs.getUserShareRightFromUserAndUserSharer(user, userSharer).getRights().contains(addRight)) {
//			throw new IllegalAccessError("Vous n'avez pas la permission");
//		}
//
//		File f = fs.saveFile(new File(null, file.getName(), new Date(), null, file.getSize(), file.getHash(), false));
//		try {
//			us.addFileToUser(userSharer, f);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return f;
//	}

	//	private static class UploadFileData {
	//		public File file;
	//	}

	@RolesAllowed({"USER","ADMIN"})
	@PutMapping(value = "filedata")//UploadFileData uploadFileData
	public File uploadFile(@RequestBody MultipartFile fileData, @RequestParam String fileHash, @RequestParam(name = "user_sharer_id", required = false) Long userSharerId) throws IOException {//, @RequestParam String fileHash @RequestBody File file
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = us.getUser(auth.getName());
		System.err.println(fileData.getOriginalFilename());

		// Sauvegarde fichier partagé
		if (userSharerId != null) {
			log.info("Saving shared file on the database");

			User userSharer;
			try {
				userSharer = us.getUser(userSharerId);
			} catch (Exception e) {
				e.printStackTrace();
				throw new IllegalAccessError("Utilisateur partageur introuvable");
			}

			if (userSharer == null) {
				throw new IllegalAccessError("Utilisateur partageur " + userSharerId + " introuvable");
			}

			if (user == userSharer) {
				throw new IllegalAccessError("Vous ne pouvez pas partager à vous même");
			}

			Right addRight = rgts.getRight("Ajouter");
			if (!usrs.getUserShareRightFromUserAndUserSharer(user, userSharer).getRights().contains(addRight)) {
				throw new IllegalAccessError("Vous n'avez pas la permission");
			}

			// On défini l'utilisateur qui va être assigné au fichier (partageur comme propriétaire)
			user = userSharer;
		}

		if (user.getOffer() == null) {
			log.error("User '{}' has no offer but an adding file request is submitted");
			return null;
		}

		System.err.println(fileHash);

		// Sauvegarde du fichier en base
		File file = fs.saveFile(new File(null, fileData.getOriginalFilename(), new Date(), null, fileData.getSize(), fileHash, false));

		try {
			us.addFileToUser(user, file);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Impossible to add file '{}' to user '{}'", file.getId(), user.getId());
			return null;
		}

		UploadFileData ufd = new UploadFileData();
		ufd.mpFile = fileData;
		ufd.fileName = String.valueOf(file.getId());
		// Sauvegarde du fichier sur le disque
		sftpUploadGateway.upload(ufd);

		return file;
	}

	@RolesAllowed({"USER","ADMIN"})
	@GetMapping(value = "file/download")
	public byte[] downloadFile(@RequestParam(name = "file_id") long fileId) throws IllegalAccessException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = us.getUser(auth.getName());
		User fileOwner = fs.getFileOwner(fileId);
		boolean hasRight = false;

		if (fileOwner == user) {
			hasRight = true;
		}

		Right downloadRight = rgts.getRight("Télécharger");
		UserShareRight usr = usrs.getUserShareRightFromUserAndUserSharer(user, fileOwner);
		// If user is in the shared user && If user has download right
		if (fileOwner.getUserShareRights().contains(usr) && usr.getRights().contains(downloadRight)) {
			//user.getFiles().contains(fs.getFile(file.getId()))
			hasRight = true;
		}

		if (hasRight) {
			try {
				return IOUtils.toByteArray(sftpUploadGateway.downloadFile(fileId));
			} catch (IOException e) {
				//e.printStackTrace();
				throw new IllegalAccessException(e.getMessage());
			}
		}

		return null;
	}

	@RolesAllowed({"USER","ADMIN"})
	@GetMapping(value = "file/checkhash")
	public byte[] checkHash(@RequestParam(name = "file_id") long fileId) throws IllegalAccessException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = us.getUser(auth.getName());
		User fileOwner = fs.getFileOwner(fileId);
		boolean hasRight = false;

		if (fileOwner == user) {
			hasRight = true;
		}

		Right checkHashRight = rgts.getRight("Vérifier intégrité");
		UserShareRight usr = usrs.getUserShareRightFromUserAndUserSharer(user, fileOwner);
		// If user is in the shared user && If user has check hash right
		if (fileOwner.getUserShareRights().contains(usr) && usr.getRights().contains(checkHashRight)) {
			hasRight = true;
		}

		File file = fs.getFile(fileId);
		if (file == null) throw new IllegalStateException("Fichier introuvable");

		if (hasRight) {
			try {
				return IOUtils.toByteArray(sftpUploadGateway.downloadFile(fileId));
			} catch (IOException e) {
				//e.printStackTrace();
				throw new IllegalAccessException(e.getMessage());
			}

			//	return this.generateHash(...);
		}

		return null;
	}

	@RolesAllowed({"USER","ADMIN"})
	@DeleteMapping(value = "deletefile", produces = MediaType.APPLICATION_JSON_VALUE)//, consumes = "application/json"
	public String removeFile(@RequestBody long fileId) throws Exception {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = us.getUser(auth.getName());
		User fileOwner = fs.getFileOwner(fileId);
		boolean hasRight = false;

		if (fileOwner == user) {
			hasRight = true;
		}

		if (!hasRight) {
			Right deleteRight = rgts.getRight("Supprimer");
			UserShareRight usr = usrs.getUserShareRightFromUserAndUserSharer(user, fileOwner);
			if (deleteRight == null || !usr.getRights().contains(deleteRight)) {
				return null;
			}
		}

		sftpUploadGateway.archiveFile(fileId);
		us.archiveUserFile(user, fileId);
		return "{\"message\":\"Le fichier a été supprimé\"}";
	}

	// Rights
	@RolesAllowed({"USER","ADMIN"})
	@GetMapping(value = "rights")
	public Collection<Right> getRights() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = us.getUser(auth.getName());
		if (user.getOffer() == null || !user.getOffer().getName().contains("Professionnel")) {
			throw new IllegalStateException("Vous devez disposer d'une offre professionnel");
		}
		return rgts.getRights();
	}

	@RolesAllowed({"USER","ADMIN"})
	@GetMapping(value = "hasright")
	public boolean hasRight(@RequestParam(name = "user_sharer_id") long userSharerId, @RequestParam(name = "right_name") String rightName) throws Exception {
		Right right = rgts.getRight(rightName);
		if (right == null) return false;

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = us.getUser(auth.getName());
		User userSharer = us.getUser(userSharerId);
		if (userSharer == null) return false;

		UserShareRight userShareRight = usrs.getUserShareRightFromUserAndUserSharer(user, userSharer);

		return userShareRight != null && userShareRight.getRights().contains(right);
	}

	@RolesAllowed({"USER","ADMIN"})
	@GetMapping(value = "userssharerights")
	public Collection<UserShareRight> getUsersSharedRights() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = us.getUser(auth.getName());
		if (user.getOffer() == null || !user.getOffer().getName().contains("Professionnel")) {
			throw new IllegalStateException("Vous devez disposer d'une offre professionnel");
		}

		return usrs.getUserShareRightsFromUserSharer(user);//Arrays.asList();
	}

	@RolesAllowed({"USER","ADMIN"})
	@GetMapping(value = "userssharer")
	public Collection<User> getUsersSharer() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = us.getUser(auth.getName());
		return us.getUsersSharer(user);
	}

	@RolesAllowed({"USER","ADMIN"})
	@PostMapping(value = "enableright")
	public UserShareRight enableRight(@RequestParam(name = "user_share_right_id") long userShareRightId, @RequestParam(name = "right_id") long rightId, @RequestParam boolean enable) {
		UserShareRight userShareRight = usrs.getUserShareRight(userShareRightId).get();
		if (userShareRight == null) {
			log.error("user_share_right '{}' not found in the database", userShareRightId);
			return null;
		}

		Right right = rgts.getRight(rightId);
		if (right == null) {
			log.error("Right '{}' not found in the database", rightId);
			return null;
		}

		if (right.getName().equalsIgnoreCase("Afficher")) {
			// On supprime dans tous les cas tous les droits puisque si appel de cette méthode, c'est qu'on souhaite désactiver la perm "Afficher" comme elle est activée par défaut
//			us.removeUserShareRight(user, userShareRight);
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			User user = us.getUser(auth.getName());
//			usrs.removeAllRightsOfUserShareRight(userShareRight);
			usrs.deleteUserShareRight(user, userShareRight);
			return null;
		}

		if (enable) {
			return usrs.addRightToUserShareRight(userShareRight, right);
		} else {
			return usrs.removeRightOfUserShareRight(userShareRight, right);
		}
	}

	@RolesAllowed({"USER","ADMIN"})
	@PostMapping(value = "usershare")
	public UserShareRight shareToUser(@RequestBody String username) {
		User userShare = us.getUser(username);
		// If the target user to share is null
		if (userShare == null) {
			log.error("User '{}' not found for sharing", username);
			return null;
		}

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth.getName().equalsIgnoreCase(username)) {
			throw new IllegalStateException("Impossible de partager des droits à soi-même");
		}
		User user = us.getUser(auth.getName());

		if (user.getOffer() == null || !user.getOffer().getName().contains("Professionnel")) {
			throw new IllegalStateException("Vous devez disposer d'une offre professionnel");
		}

		//usrs.saveUserShareRight(usr);
		return usrs.shareRightsToUser(user, userShare, null);
	}

	// OFFERS
	@GetMapping(value = "offers")
	public Collection<Offer> getOffers() {
		return os.getOffers();
	}

	@RolesAllowed({"USER","ADMIN"})
	@GetMapping(value = "offer")
	public Offer getOffer(@RequestParam long id) {
		return os.getOffer(id);
	}

	//	@RolesAllowed("USER")
	//	@PostMapping(value = "congesrequest", consumes = "application/json")
	//	public void congesRequest(@RequestBody File file) {
	//		file.setCreationDate(new Date());
	//		// Avoid bypass of admin validation
	////		conge.setValidated(false);
	//
	//		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	//		// Add conges request from username of authenticated user
	////		us.addCongeToUser(us.getUser(auth.getName()), conge);
	//	}

	//	@RolesAllowed({"USER","ADMIN"})
	//	@GetMapping(value = "newusers")
	//	public Collection<User> getNewUsers() {
	//		List<User> users = new ArrayList<>();
	//		Calendar currentCal = Calendar.getInstance();
	//		Calendar cal = Calendar.getInstance();
	//
	//		// Get users registered this year
	//		us.getUsers().stream().forEach(u -> {
	//			cal.setTime(u.getCreationDate());
	//			boolean isNewUser = ((currentCal.get(Calendar.YEAR) - cal.get(Calendar.YEAR)) <= 1);
	//			if (isNewUser) {
	//				// Avoid sending password, ...
	//				User us = new User(u.getId(), null, null, null, u.getUsername(), null, u.getCreationDate(), true, u.isAccountActive(), null, null, null);
	//				users.add(us);
	//			}
	//		});
	//
	//		return users;
	//	}

	@RolesAllowed({"USER","ADMIN"})
	@PostMapping(value = "darkmode")
	public void changeDarkMode(@RequestBody boolean darkModeEnabled) throws Exception {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		us.setDarkMode(us.getUser(auth.getName()), darkModeEnabled);
	}

	@RolesAllowed({"USER","ADMIN"})
	@PostMapping(value = "order")
	public void sendOrder(@RequestBody Order order) throws Exception {
		log.info("Saving order {} on the database",  order.getPaypalId());

		// Order already exists
		if (ors.getOrder(order.getPaypalId()) != null) {
			log.error("An order with PayPal ID {} already exists", order.getPaypalId());
			return;
		}

		try {
			order.setOffer(PayPalService.getOrderOffer(order.getPaypalId()));
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			log.error("Impossible to get offer of order '{}' from the paypal order", order.getPaypalId());
			return;
		}

		order = ors.saveOrder(order);

		// // Avoid bypass of admin validation
		// conge.setValidated(false);

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = us.getUser(auth.getName());

		if (user == null) {
			log.error("User '{}' doesn't exists on the database", auth.getName());
			return;
		}

		// Add order to authenticated user from his username
		us.addOrderToUser(user, order);
		// Ajout/changement offre utilisateur
		us.setOffer(user, order.getOffer());
	}

}
