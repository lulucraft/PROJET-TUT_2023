package fr.nepta.cloud;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import fr.nepta.cloud.model.File;
import fr.nepta.cloud.model.Offer;
import fr.nepta.cloud.model.Order;
import fr.nepta.cloud.model.Role;
import fr.nepta.cloud.model.User;
import fr.nepta.cloud.service.FileService;
import fr.nepta.cloud.service.OfferService;
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
	CommandLineRunner run(RoleService rs, UserService us, FileService ns, OfferService os) {
		return args -> {
			// ROLES
			if (rs.getRole("USER") == null) {
				rs.saveRole(new Role(null, "USER"));
			}
			if (rs.getRole("ADMIN") == null) {
				rs.saveRole(new Role(null, "ADMIN"));
			}

			if (os.getOffer("Particulier") == null) {
				Set<String> advantages = new HashSet<>();
				advantages.add("Tous les formats de fichiers");

				Set<String> disadvantages = new HashSet<>();
				disadvantages.add("Pas de partage de fichiers");

				os.saveOffer(new Offer(null, "Particulier", 10, advantages, disadvantages));
			}

			if (os.getOffer("Professionnel") == null) {
//				System.err.println(advRepor.getAdvantagesByName("Tous les formats de fichiers"));
				Set<String> advantages = new HashSet<>();
				advantages.add("Tous les formats de fichiers");
				advantages.add("Partage fichiers illimité");

				os.saveOffer(new Offer(null, "Professionnel", 20, advantages, new HashSet<>()));
			}

			// Create default users
			if (us.getUser("admin") == null) {
				Offer offer = null;
				List<Order> orders = null;
				us.saveUser(new User(null, null, null, "admin@gmail.com", "admin", "root", new Date(), true, true, offer, orders, new ArrayList<Role>(), new ArrayList<File>()));
			}
			if (!us.getUser("admin").getRoles().contains(rs.getRole("ADMIN"))) {
				us.addRoleToUser("admin", "ADMIN");
			}

			if (us.getUser("user") == null) {
				Offer offer = null;
				List<Order> orders = null;
				us.saveUser(new User(null, null, null, "user@gmail.com", "user", "azerty", new Date(), true, true, offer, orders, new ArrayList<Role>(), new ArrayList<File>()));
			}
			if (!us.getUser("user").getRoles().contains(rs.getRole("USER"))) {
				us.addRoleToUser("user", "USER");
			}

			// NEWSLETTER
			if (ns.getFile("Fichier1") == null) {
				ns.saveFile(new File(null, "Fichier1", new Date(), null, 0, "fezfezzef"));
			}
			if (ns.getFile("Fichier2") == null) {
				ns.saveFile(new File(null, "Fichier2", new Date(), null, 1, "rhtrgtrgrg"));
			}
		};
	}

	@Bean
	JavaMailSender getJavaMailSender() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost("ssl0.ovh.net");
		mailSender.setPort(587);

		mailSender.setUsername("admin@juline.tech");
		mailSender.setPassword("MyS_fztv6TCN5mt!");

//		Properties props = mailSender.getJavaMailProperties();
//		props.put("mail.smtp.host", "ssl0.ovh.net");
////		props.put("mail.smtp.ssl.trust", "ssl0.ovh.net");
////		props.put("mail.transport.protocol", "smtp");
////		props.put("mail.smtp.ssl.protocols", "TLSv1.2");
//		props.put("mail.smtp.auth", "true");
////		props.put("mail.smtp.user", "admin@juline.tech");
////		props.put("mail.smtp.password", "MyS_fztv6TCN5mt!");
////		props.put("mail.smtp.port", "587");
//		props.put("mail.smtp.starttls.enable", "true");
//		props.put("mail.smtp.starttls.required", "true");
////		props.put("mail.smtp.ssl.enable", "true");
//		props.put("mail.debug", "false");

		return mailSender;
	}

}
