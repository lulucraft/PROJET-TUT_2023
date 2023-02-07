package fr.nepta.intranet.service.impl;

import java.util.Collection;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.nepta.intranet.model.Conge;
import fr.nepta.intranet.repository.CongeRepo;
import fr.nepta.intranet.service.CongeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor @Slf4j
@Transactional
public class CongeServiceImpl implements CongeService {

	private final CongeRepo congeRepo;

	@Override
	public Conge saveConge(Conge conge) {
		log.info("Saving new conge to the database");
		return congeRepo.save(conge);
	}

	@Override
	public Collection<Conge> getConges() {
		log.info("Fetching conges from database");
		return congeRepo.findAll();
	}

	@Override
	public Conge getConge(long congeId) {
		log.info("Fetching conge '{}' from database", congeId);
		return congeRepo.findById(congeId);
	}

	@Override
	public void validateConge(Conge conge) {
		congeRepo.save(conge);
//		conge = congeRepo.getById(conge.getId());
//		congeRepo.save(conge);
		log.info("Validate conge '{}'", conge.getId());
	}


//	@Override
//	public Conge getConge(String congeName) {
//		log.info("Fetching role '{}'", congeName);
//		return roleRepo.findByName(congeName);
//	}

}
