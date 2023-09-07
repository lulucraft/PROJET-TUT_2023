package fr.nepta.cloud.security;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.nepta.cloud.CloudApplication;
import fr.nepta.cloud.service.MailService;
import fr.nepta.cloud.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private HashMap<Long, String> OTPCodes = new HashMap<>();

	private AuthenticationManager authenticationManager;
	private final UserService us;
	private final MailService mailS;

	public AuthenticationFilter(AuthenticationManager am, ApplicationContext ctx) {
		this.authenticationManager = am;
		this.us = ctx.getBean(UserService.class);
		this.mailS = ctx.getBean(MailService.class);
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

		String username = request.getParameter(SPRING_SECURITY_FORM_USERNAME_KEY);
		String password = request.getParameter(SPRING_SECURITY_FORM_PASSWORD_KEY);

		log.info("Try auth of {}", username);

		UsernamePasswordAuthenticationToken upaToken = new UsernamePasswordAuthenticationToken(username, password);

		return authenticationManager.authenticate(upaToken);
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication auth) throws IOException, ServletException {
		User authUser = (User) auth.getPrincipal();
		Algorithm algo = Algorithm.HMAC256(CloudApplication.SECRET.getBytes());
		fr.nepta.cloud.model.User user = us.getUser(authUser.getUsername());

		// Account disabled
		if (user != null && !user.isAccountActive()) {
			log.error("User '{}' cannot log in. User disabled", user.getId());
			return;
		}

		// User not in OTP list
		if (!this.OTPCodes.containsKey(user.getId())) {
			String otpCode = generateOTPCode();
			mailS.sendOTPMail(user.getEmail(), otpCode);
			this.OTPCodes.put(user.getId(), otpCode);
			log.info(otpCode);
			log.info("User '{}' added in otp list.", user.getId());
			return;
		}

		// Invalid OTP code
		if (!this.OTPCodes.get(user.getId()).equalsIgnoreCase(request.getParameter("otp_code"))) {
			this.OTPCodes.remove(user.getId());
			log.error("User '{}' failed to enter otp code.", user.getId());
			return;
		}

		// OTP code valid
		this.OTPCodes.remove(user.getId());
		log.info("User '{}' successfully enter otp code.", user.getId());

		String accessToken = JWT.create()
				.withSubject(user.getUsername())
				.withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
				.withIssuer(request.getRequestURI().toString())
				.withClaim("roles", authUser.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
				.withClaim("creation_date", user.getCreationDate())
				.withClaim("dark_mode_enabled", user.isDarkModeEnabled())
				.withClaim("account_active", user.isAccountActive())
				.withClaim("offer", user.getOffer() != null ? user.getOffer().getName() : null)
				.sign(algo);

		String refreshToken = JWT.create()
				.withSubject(user.getUsername())
				.withExpiresAt(new Date(System.currentTimeMillis() + 40 * 2 * 60 * 1000))
				.withIssuer(request.getRequestURI().toString())
				.sign(algo);

		response.setHeader("accessToken", accessToken);
		response.setHeader("refreshToken", refreshToken);

		Map<String, String> tokens = new HashMap<>();
		tokens.put("accessToken", accessToken);
		tokens.put("refreshToken", refreshToken);

		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		//		response.setHeader("Access-Control-Allow-Origin", "*");
		new ObjectMapper().writeValue(response.getOutputStream(), tokens);
	}

	private String generateOTPCode() {
		int leftLimit = 48; // numeral '0'
		int rightLimit = 122; // letter 'z'
		int targetStringLength = 10;
		Random random = new Random();

		String generatedString = random.ints(leftLimit, rightLimit + 1)
				.filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
				.limit(targetStringLength)
				.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
				.toString();
		return generatedString;
	}

}
