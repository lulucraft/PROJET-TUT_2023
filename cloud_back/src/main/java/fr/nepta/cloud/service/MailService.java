package fr.nepta.cloud.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class MailService {

	@Autowired
	private JavaMailSender mailSender;

	public void sendMail(String email, String newPassword) {
		SimpleMailMessage message = new SimpleMailMessage();

		message.setFrom("noreply@tuxit.site");
		message.setTo("lucas.besson@outlook.fr");
		message.setSubject("Réinitialisation de votre mot de passe Tuxit Cloud");
        message.setText("Votre nouveau mot de passe de connexion à Tuxit Cloud : " + newPassword);

		this.mailSender.send(message);
	}
}
