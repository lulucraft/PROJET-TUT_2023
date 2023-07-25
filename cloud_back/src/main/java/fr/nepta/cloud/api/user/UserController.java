package fr.nepta.cloud.api.user;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import fr.nepta.cloud.model.File;
import fr.nepta.cloud.model.Offer;
import fr.nepta.cloud.service.FileService;
import fr.nepta.cloud.service.OfferService;
import fr.nepta.cloud.service.UserService;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
//@CrossOrigin(origins = "https://intranet.tracroute.lan/", maxAge = 3600)
@CrossOrigin(origins = {"*"}, maxAge = 4800, allowCredentials = "false", methods = { RequestMethod.GET, RequestMethod.OPTIONS, RequestMethod.POST, RequestMethod.PUT })
@RestController
@RequestMapping("api/user/")
public class UserController {

	@Autowired
	private final UserService us;
	@Autowired
	private final FileService fs;
	@Autowired
	private final OfferService os;

	//	@GetMapping(value = "users")
	//	public String getUsers() {
	//		return us.getUsers().toString();
	//	}

	@RolesAllowed({"USER","ADMIN"})
	@GetMapping(value = "files")
	public Collection<File> getFiles() {//@RequestParam String username
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return us.getUser(auth.getName()).getFiles();
	}

	@RolesAllowed({"USER","ADMIN"})
	@PutMapping(value = "file")
	public File addFile(@RequestBody File file) {//@RequestBody File file
		System.err.println(file);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		File f = fs.saveFile(new File(null, file.getName(), new Date(), null, file.getSize(), file.getHash()));
		us.addFileToUser(us.getUser(auth.getName()), f);
		// Sauvegarde du fichier sur le disque
		// TODO
		return f;
	}

	@RolesAllowed({"USER","ADMIN"})
	@PutMapping(value = "filedata")
	public File uploadFile(@RequestBody MultipartFile mpFile) {//@RequestBody File file
		System.err.println(mpFile);
		// Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		// Sauvegarde du fichier sur le disque
		// TODO
		return null;
	}

	@RolesAllowed({"USER","ADMIN"})
	@GetMapping(value = "file/download")
	public File downloadFile(@RequestBody File file) {
		// Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		fs.getFile(file.getId());
		return null;
	}

	@RolesAllowed({"USER","ADMIN"})
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

	@RolesAllowed("USER")
	@PostMapping(value = "deletefile", consumes = "application/json", produces = MediaType.APPLICATION_JSON_VALUE)
	public String removeFile(@RequestBody long fileId) throws Exception {
		//		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		//		us.removeFileFromUser(us.getUser(auth.getName()), fileId);
		return "{\"message\":\"Le fichier a été supprimé\"}";
	}

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

	@PostMapping(value = "darkmode")
	public void changeDarkMode(@RequestBody boolean darkModeEnabled) throws Exception {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		us.setDarkMode(us.getUser(auth.getName()), darkModeEnabled);
	}
	
}
