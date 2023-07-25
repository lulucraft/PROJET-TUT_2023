package fr.nepta.cloud.service.impl;

import java.util.Collection;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.nepta.cloud.model.Offer;
import fr.nepta.cloud.repository.OfferRepo;
import fr.nepta.cloud.service.OfferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OfferServiceImpl implements OfferService {

	private final OfferRepo offerRepo;

	@Override
	public Offer saveOffer(Offer offer) {
		log.info("Saving new offer to the database");
		return offerRepo.save(offer);
	}

	@Override
	public Offer getOffer(String name) {
		log.info("Fetching offer '{}'", name);
		return offerRepo.findByName(name);
	}

	@Override
	public Offer getOffer(long id) {
		log.info("Fetching offer '{}'", id);
		return offerRepo.findById(id).get();
	}

	@Override
	public Collection<Offer> getOffers() {
		log.info("Fetching offers");
		return offerRepo.findAll();
	}

}
