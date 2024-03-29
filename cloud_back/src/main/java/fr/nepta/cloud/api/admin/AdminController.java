package fr.nepta.cloud.api.admin;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.nepta.cloud.model.User;
import fr.nepta.cloud.service.UserService;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
//@CrossOrigin(origins = "https://back.intranet.tracroute.lan/", maxAge = 3600)
@CrossOrigin(origins = {"https://tuxit.site/", "51.79.109.241"}, maxAge = 4800, allowCredentials = "false", methods = { RequestMethod.GET, RequestMethod.OPTIONS, RequestMethod.POST, RequestMethod.PUT })
@RestController
@RequestMapping("api/admin/")
public class AdminController {

	@Autowired
	private final UserService us;
//	@Autowired
//	private final RoleService rs;
//	@Autowired
//	private final FileService ns;

	@RolesAllowed("ADMIN")
	@GetMapping(value = "users")
	public Collection<User> getUsers() {
		return us.getUsers();
	}

	@RolesAllowed("ADMIN")
	@GetMapping(value = "user")
	public User getUser(@RequestParam long userId) throws Exception {
		return us.getUser(userId);
	}

//	@RolesAllowed("ADMIN")
//	@GetMapping(value = "conges")
//	public Map<String, Collection<File>> getAllConges() {
//		Map<String, Collection<File>> conges = new HashMap<>();
//		for (User u : us.getUsers()) {
//			if (u.getRoles().contains(rs.getRole("USER"))) {
//				conges.put(u.getUsername(), u.getConges());
//			}
//		}
//		return conges;
//	}

//	@RolesAllowed("ADMIN")
//	@PostMapping(value = "editnewsletter")
//	public void editNewsletter(@RequestBody Newsletter newsletter) {
//		Newsletter nl = ns.getNewsletter(newsletter.getType());
//		if (nl != null) {
//			newsletter.setId(nl.getId());
//		}
//		ns.saveNewsletter(newsletter);
//	}

	@RolesAllowed("ADMIN")
	@PostMapping(value = "edituser", consumes = "application/json")
	public void editUser(@RequestBody User user) throws Exception {
		us.editUser(user);
	}

}
