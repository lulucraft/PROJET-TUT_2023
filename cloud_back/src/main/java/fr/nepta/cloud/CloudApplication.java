package fr.nepta.cloud;

import java.util.ArrayList;
import java.util.Date;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import fr.nepta.cloud.model.File;
import fr.nepta.cloud.model.Offer;
import fr.nepta.cloud.model.Role;
import fr.nepta.cloud.model.User;
import fr.nepta.cloud.service.FileService;
import fr.nepta.cloud.service.RoleService;
import fr.nepta.cloud.service.UserService;

@SpringBootApplication
@EnableJpaRepositories
public class CloudApplication {

	public static final String SECRET = "E)H@McQfTjWnZr4u7x!A%D*F-JaNdRgU";

	public static void main(String[] args) {
		SpringApplication.run(CloudApplication.class, args);
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	CommandLineRunner run(UserService us, RoleService rs, FileService ns) {
		return args -> {
			// ROLES
			if (rs.getRole("USER") == null) {
				rs.saveRole(new Role(null, "USER"));
			}
			if (rs.getRole("ADMIN") == null) {
				rs.saveRole(new Role(null, "ADMIN"));
			}

			// Create default users
			if (us.getUser("admin") == null) {
				Offer offer = null;
				us.saveUser(new User(null, null, null, "admin@gmail.com", "admin", "root", new Date(), true, true, offer, new ArrayList<Role>(), new ArrayList<File>()));
			}
			if (!us.getUser("admin").getRoles().contains(rs.getRole("ADMIN"))) {
				us.addRoleToUser("admin", "ADMIN");
			}

			if (us.getUser("user") == null) {
				Offer offer = null;
				us.saveUser(new User(null, null, null, "user@gmail.com", "user", "azerty", new Date(), true, true, offer, new ArrayList<Role>(), new ArrayList<File>()));
			}
			if (!us.getUser("user").getRoles().contains(rs.getRole("USER"))) {
				us.addRoleToUser("user", "USER");
			}

			// NEWSLETTER
			if (ns.getFile("test") == null) {
				ns.saveFile(new File(null, "Fichier1", null, new Date()));
			}
			if (ns.getFile("SELLS") == null) {
				ns.saveFile(new File(null, "Fichier2", null, new Date()));
			}
		};
	}

}
