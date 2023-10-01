package fr.nepta.cloud.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class MailService {

	@Autowired
	private JavaMailSender mailSender;

	public void sendMail(String email, String mailObject, String mailContent) {
		SimpleMailMessage message = new SimpleMailMessage();

		message.setFrom("noreply@tuxit.site");
//		message.setTo("lucas.besson@outlook.fr");
		message.setTo(email);
		message.setSubject(mailObject);
        message.setText(mailContent);

		this.mailSender.send(message);
	}

	public void sendNewPasswordMail(String email, String newPassword) {
		sendMail(email, "Réinitialisation de votre mot de passe Tuxit Cloud", "Votre nouveau mot de passe de connexion à Tuxit Cloud : " + newPassword);
	}

	public void sendOTPMail(String email, String otpCode) {
		sendMail(email, "Code d'authentification Tuxit Cloud", "Votre code à usage unique (1 essai) : " + otpCode);
	}
}
