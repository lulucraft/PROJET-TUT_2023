package fr.nepta.cloud.service;

import java.util.Collection;

import fr.nepta.cloud.model.Offer;

public interface OfferService {

	Offer saveOffer(Offer offer);

	Offer getOffer(String name);

	Offer getOffer(long id);

	Collection<Offer> getOffers();

}
