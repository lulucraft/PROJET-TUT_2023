package fr.nepta.cloud.api.auth;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.nepta.cloud.CloudApplication;
import fr.nepta.cloud.model.Role;
import fr.nepta.cloud.model.User;
import fr.nepta.cloud.service.MailService;
import fr.nepta.cloud.service.UserService;
import fr.nepta.cloud.service.UserShareRightService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@RequiredArgsConstructor @Log4j2
@CrossOrigin(origins = {"https://tuxit.site/", "51.79.109.241"}, maxAge = 3600, allowCredentials = "false", methods = { RequestMethod.GET, RequestMethod.OPTIONS, RequestMethod.POST, RequestMethod.PUT })
@RestController
@RequestMapping("api/auth/")
public class AuthController {

	private final UserService us;
	private final UserShareRightService usrs;

	@Autowired
	private final MailService mailS;

	@GetMapping(value = "refreshtoken")//consumes = "application/json", 
	public void refreshToken(HttpServletRequest request, HttpServletResponse response) {
		String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			try {
				String refreshToken = authorizationHeader.substring("Bearer ".length());
				Algorithm algo = Algorithm.HMAC256(CloudApplication.SECRET.getBytes());

				JWTVerifier verifier = JWT.require(algo).build();
				DecodedJWT dJWT = verifier.verify(refreshToken);

				String username = dJWT.getSubject();
				User user = us.getUser(username);

				String accessToken = JWT.create()
						.withSubject(user.getUsername())
						.withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
						.withIssuer(request.getRequestURI().toString())
						.withClaim("roles", user.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
						.withClaim("creation_date", user.getCreationDate())
						.withClaim("dark_mode_enabled", user.isDarkModeEnabled())
						.withClaim("account_active", user.isAccountActive())
						.withClaim("offer", user.getOffer() != null ? user.getOffer().getName() : null)
						.withClaim("shared", usrs.hasShareRights(user))
						.sign(algo);

				refreshToken = JWT.create()
						.withSubject(user.getUsername())
						.withExpiresAt(new Date(System.currentTimeMillis() + 40 * 2 * 60 * 1000))
						.withIssuer(request.getRequestURI().toString())
						.sign(algo);

				Map<String, String> tokens = new HashMap<>();
				tokens.put("accessToken", accessToken);
				tokens.put("refreshToken", refreshToken);

				response.setContentType(MediaType.APPLICATION_JSON_VALUE);
				new ObjectMapper().writeValue(response.getOutputStream(), tokens);

				log.info("Refresh token for user: {}", username);
			} catch(Exception e) {
				log.error("Refreshtoken error: {}", e.getMessage());
			}
		} else {
			throw new RuntimeException("Refresh token is missing");
		}
	}

	@Getter @Setter
	private static class RegisterUser {
		private String email, username, password;
	}

	@PostMapping(value = "register", consumes = "application/json")
	public String register(@RequestBody RegisterUser regUser) {
		if (regUser.getUsername() == null || regUser.getUsername().trim().isEmpty()) {
			throw new IllegalStateException("Nom d'utilisateur manquant");
		}

		if (regUser.getPassword() == null || regUser.getPassword().trim().isEmpty()) {
			throw new IllegalStateException("Mot de passe manquant");
		}

		boolean userExists = us.getUser(regUser.getUsername()) != null;
		if (userExists) {
			throw new IllegalStateException("Ce nom d'utilisateur est déjà utilisé");
		}

		// User creation date
		//user.setCreationDate(new Date());

		User user = us.saveUser(new User(null, null, null, regUser.getEmail(), regUser.getUsername(), regUser.getPassword(), new Date(), false, true, null, null, new ArrayList<>(), null, null));
		// Add user role to User by default
		us.addRoleToUser(regUser.getUsername(), "USER");

		return user.getId().toString();
	}

	@PostMapping(value = "resetpassword")
	public void resetPassword(@RequestBody String email) throws Exception {
		User user = us.getUserFromEmail(email);

		if (user == null) return;

		// Generate new random password
		String newPassword = getRandomSpecialChars(13).collect(Collectors.toList()).stream().map(String::valueOf).collect(Collectors.joining());
		user.setPassword(newPassword);// Password auto encoded with bcrypt

		us.saveUser(user);
		// Send mail to user which contains the new password
		mailS.sendNewPasswordMail(email, newPassword);
	}

	private static class EditPasswordData {
		public String oldPassword, newPassword;
	}

	@PostMapping(value = "editpassword")
	public void editPassword(@RequestBody EditPasswordData editPasswordData) throws Exception {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = us.getUser(auth.getName());
		if (user == null) return;

		if (!BCrypt.checkpw(editPasswordData.oldPassword, user.getPassword())) {
			log.error("L'ancien mot de passe ne correspond pas");
			return;
		}

		user.setPassword(editPasswordData.newPassword);// Password auto encoded with bcrypt

		us.saveUser(user);

		log.info("Password of user '{}' updated", user.getId());
	}

	private Stream<Character> getRandomSpecialChars(int count) {
		Random random = new SecureRandom();
		IntStream specialChars = random.ints(count, 33, 45);
		return Stream.concat(specialChars.mapToObj(data -> (char) data), Stream.of((char) (new Random().nextInt(26) + 'a')));
	}

	//	@PostMapping(value = "login", consumes = "application/json", produces = "application/json")
	//	public ResponseEntity<String> authenticateUser(@RequestBody User user) {
	//		System.err.println(user.getUsername());
	//		System.err.println(user.getPassword());
	//
	//		return ResponseEntity.ok().body("test");
	//	}

}
