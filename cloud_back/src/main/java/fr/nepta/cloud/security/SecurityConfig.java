package fr.nepta.cloud.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final UserDetailsService userDetailsService;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
    private ApplicationContext applicationContext;

//	@Override
//	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
////		auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
//		auth.authenticationProvider(daoAuthenticationProvider());
//
////		String pass = passwordEncoder().encode("test");
////		System.err.println(pass);
////		auth.inMemoryAuthentication().withUser("admin").password(pass).roles("ADMIN");
//	}

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.cors((cors) -> cors.configure(http));
		http.csrf((csrf) -> csrf.disable());

		http.authenticationProvider(daoAuthenticationProvider());

		http.sessionManagement((sessionManagement) -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		http.authorizeHttpRequests((authorizeHttpRequests) ->
			// ALL
			authorizeHttpRequests.requestMatchers("/api/auth/**").permitAll()
			// USER
			.requestMatchers(HttpMethod.GET, "/api/users").hasAnyAuthority("USER")
			// ADMIN
			.requestMatchers(HttpMethod.POST, "/api/users").hasAnyAuthority("USER")
			.requestMatchers(HttpMethod.GET, "/api/admin/**").hasAnyAuthority("ADMIN")
			.requestMatchers(HttpMethod.POST, "/api/admin/**").hasAnyAuthority("ADMIN")
			.anyRequest().authenticated()
		);

		// Logout URL
		http.logout((logoutCustomizer) -> logoutCustomizer.logoutUrl("/api/auth/logout").deleteCookies("JSESSIONID"));

		// USER
//		http.authorizeHttpRequests().antMatchers(HttpMethod.GET, "/api/users").hasAnyAuthority("USER");
//		http.authorizeHttpRequests().antMatchers(HttpMethod.POST, "/api/users").hasAnyAuthority("USER");

		// ADMIN
//		http.authorizeHttpRequests().antMatchers(HttpMethod.GET, "/api/admin/**").hasAnyAuthority("ADMIN");
//		http.authorizeHttpRequests().antMatchers(HttpMethod.POST, "/api/admin/**").hasAnyAuthority("ADMIN");

//		http.authorizeHttpRequests().anyRequest().authenticated().and().logout().logoutUrl("/api/auth/logout").deleteCookies("JSESSIONID");

		AuthenticationFilter authFilter = new AuthenticationFilter(authenticationManager(null), applicationContext);
		authFilter.setFilterProcessesUrl("/api/auth/login");

		http.addFilter(authFilter);
		http.addFilterBefore(new AuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean
	DaoAuthenticationProvider daoAuthenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setPasswordEncoder(bCryptPasswordEncoder);
		provider.setUserDetailsService(userDetailsService);
		return provider;
	}
}
