package fr.nepta.intranet.api.user;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.annotation.security.RolesAllowed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.nepta.intranet.model.Conge;
import fr.nepta.intranet.model.Newsletter;
import fr.nepta.intranet.model.User;
import fr.nepta.intranet.service.NewsletterService;
import fr.nepta.intranet.service.UserService;
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
	private final NewsletterService ns;

	//	@GetMapping(value = "users")
	//	public String getUsers() {
	//		return us.getUsers().toString();
	//	}

	@RolesAllowed({"USER","ADMIN"})
	@GetMapping(value = "congesacquis")
	public double getCongesAcquis(@RequestParam String username) {
		return us.getUser(username).getCongesNbr();
	}

	@RolesAllowed({"USER","ADMIN"})
	@GetMapping(value = "conges")
	public Collection<Conge> getConges(@RequestParam String username) {
		return us.getUser(username).getConges();
	}

	@RolesAllowed("USER")
	@PostMapping(value = "congesrequest", consumes = "application/json")
	public void congesRequest(@RequestBody Conge conge) {
		conge.setCreationDate(new Date());
		// Avoid bypass of admin validation
		conge.setValidated(false);

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		// Add conges request from username of authenticated user
		us.addCongeToUser(us.getUser(auth.getName()), conge);
	}

	@RolesAllowed("USER")
	@PostMapping(value = "deletecongesrequest", consumes = "application/json", produces = MediaType.APPLICATION_JSON_VALUE)
	public String deleteCongesRequest(@RequestBody long congeId) throws Exception {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		us.deleteCongeFromUser(us.getUser(auth.getName()), congeId);
		return "{\"message\":\"La demande de congés a été supprimée\"}";
	}

	@RolesAllowed({"USER","ADMIN"})
	@GetMapping(value = "newusers")
	public Collection<User> getNewUsers() {
		List<User> users = new ArrayList<>();
		Calendar currentCal = Calendar.getInstance();
		Calendar cal = Calendar.getInstance();

		// Get users registered this year
		us.getUsers().stream().forEach(u -> {
			cal.setTime(u.getCreationDate());
			boolean isNewUser = ((currentCal.get(Calendar.YEAR) - cal.get(Calendar.YEAR)) <= 1);
			if (isNewUser) {
				// Avoid sending password, ...
				User us = new User(u.getId(), null, null, null, u.getUsername(), null, u.getCreationDate(), 0, true, u.isAccountActive(), null, null);
				users.add(us);
			}
		});

		return users;
	}

	@PostMapping(value = "darkmode")
	public void changeDarkMode(@RequestBody boolean darkModeEnabled) throws Exception {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		us.setDarkMode(us.getUser(auth.getName()), darkModeEnabled);
	}

	@GetMapping(value = "newsletters")
	public Collection<Newsletter> getNewsletters() {
		return ns.getNewsletters();
	}

	@GetMapping(value = "newsletter")
	public Newsletter getNewsletter(@RequestParam String newsletterType) {
		return ns.getNewsletter(newsletterType);
	}

}
