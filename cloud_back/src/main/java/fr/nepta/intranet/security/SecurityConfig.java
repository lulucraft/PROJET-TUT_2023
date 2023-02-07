package fr.nepta.intranet.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private final UserDetailsService userDetailsService;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//		auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
		auth.authenticationProvider(daoAuthenticationProvider());

//		String pass = passwordEncoder().encode("test");
//		System.err.println(pass);
//		auth.inMemoryAuthentication().withUser("admin").password(pass).roles("ADMIN");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.cors();
		//http.authorizeHttpRequests().anyRequest().authenticated().and().formLogin();
//		http.csrf().ignoringAntMatchers("/api/auth/register", "/api/auth/login", "/api/auth/logout");//.disable();
		http.csrf().disable();
		//http.authorizeHttpRequests().anyRequest().permitAll();
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

		// ALL
		http.authorizeHttpRequests().antMatchers("/api/auth/**").permitAll();

		// USER
		http.authorizeHttpRequests().antMatchers(HttpMethod.GET, "/api/users").hasAnyAuthority("USER");
		http.authorizeHttpRequests().antMatchers(HttpMethod.POST, "/api/users").hasAnyAuthority("USER");

		// ADMIN
		http.authorizeHttpRequests().antMatchers(HttpMethod.GET, "/api/admin/**").hasAnyAuthority("ADMIN");
		http.authorizeHttpRequests().antMatchers(HttpMethod.POST, "/api/admin/**").hasAnyAuthority("ADMIN");

		http.authorizeHttpRequests().anyRequest().authenticated()
		.and().logout().logoutUrl("/api/auth/logout").deleteCookies("JSESSIONID");

		AuthenticationFilter authFilter = new AuthenticationFilter(authenticationManagerBean(), getApplicationContext());
		authFilter.setFilterProcessesUrl("/api/auth/login");

		http.addFilter(authFilter);
		http.addFilterBefore(new AuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

    @Bean
    DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(bCryptPasswordEncoder);
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }
}
