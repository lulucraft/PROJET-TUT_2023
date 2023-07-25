package fr.nepta.cloud.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.nepta.cloud.model.Offer;

public interface OfferRepo extends JpaRepository<Offer, Long> {

	Offer findByName(String name);

}
