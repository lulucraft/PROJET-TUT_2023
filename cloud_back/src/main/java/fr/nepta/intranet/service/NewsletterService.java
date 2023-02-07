package fr.nepta.intranet.service;

import java.util.Collection;

import fr.nepta.intranet.model.Newsletter;

public interface NewsletterService {

	Newsletter saveNewsletter(Newsletter newsletter);

	Collection<Newsletter> getNewsletters();

	Newsletter getNewsletter(String newsletterType);

}
