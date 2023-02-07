package fr.nepta.intranet.service.impl;

import java.util.Collection;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.nepta.intranet.model.Newsletter;
import fr.nepta.intranet.repository.NewsletterRepo;
import fr.nepta.intranet.service.NewsletterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor @Slf4j
@Transactional
public class NewsletterServiceImpl implements NewsletterService {

	private final NewsletterRepo newsletterRepo;

	@Override
	public Newsletter saveNewsletter(Newsletter newsletter) {
		log.info("Saving Newsletter to the database");
		return newsletterRepo.save(newsletter);
	}

	@Override
	public Collection<Newsletter> getNewsletters() {
		log.info("Fetching Newsletters from the database");
		return newsletterRepo.findAll();
	}

	@Override
	public Newsletter getNewsletter(String newsletterType) {
		for (Newsletter nl : newsletterRepo.findAll()) {
			if (nl.getType().equalsIgnoreCase(newsletterType)) {
				return nl;
			}
		}

		log.info("Fetching newsletter '{}'", newsletterType);
		return null;
	}

}
