package fr.nepta.intranet.service;

import java.util.Collection;

import fr.nepta.intranet.model.Conge;

public interface CongeService {

	Conge saveConge(Conge conge);

	Collection<Conge> getConges();

	Conge getConge(long congeId);

	void validateConge(Conge conge);

//	Conge getConge(long id);

}
