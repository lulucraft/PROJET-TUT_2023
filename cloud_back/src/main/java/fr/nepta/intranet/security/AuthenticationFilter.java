package fr.nepta.intranet.security;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

import fr.nepta.intranet.IntranetApplication;
import fr.nepta.intranet.service.UserService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private AuthenticationManager authenticationManager;

	private final UserService us;

	public AuthenticationFilter(AuthenticationManager am, ApplicationContext ctx) {
		this.authenticationManager = am;
		this.us = ctx.getBean(UserService.class);
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
//		Enumeration<String> a = request.getAttributeNames();
//		for (; a.hasMoreElements(); a.nextElement()) {
//			System.err.println(a.nextElement());
//		}

		String username = request.getParameter(SPRING_SECURITY_FORM_USERNAME_KEY);
		String password = request.getParameter(SPRING_SECURITY_FORM_PASSWORD_KEY);

		log.info("Try auth of {}", username);

		UsernamePasswordAuthenticationToken upaToken = new UsernamePasswordAuthenticationToken(username, password);

		return authenticationManager.authenticate(upaToken);
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication auth) throws IOException, ServletException {
		User authUser = (User) auth.getPrincipal();
		Algorithm algo = Algorithm.HMAC256(IntranetApplication.SECRET.getBytes());
		fr.nepta.intranet.model.User user = us.getUser(authUser.getUsername());

		// Account disabled
		if (user != null && !user.isAccountActive()) {
			log.error("User '{}' cannot log in. User disabled", user.getId());
			return;
		}

		String accessToken = JWT.create()
				.withSubject(user.getUsername())
				.withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
				.withIssuer(request.getRequestURI().toString())
				.withClaim("roles", authUser.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
				.withClaim("creation_date", user.getCreationDate())
				.withClaim("dark_mode_enabled", user.isDarkModeEnabled())
				.withClaim("account_active", user.isAccountActive())
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

}
