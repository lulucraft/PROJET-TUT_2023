package fr.nepta.cloud.service;

import java.util.Collection;

import fr.nepta.cloud.model.Newsletter;

public interface NewsletterService {

	Newsletter saveNewsletter(Newsletter newsletter);

	Collection<Newsletter> getNewsletters();

	Newsletter getNewsletter(String newsletterType);

}
