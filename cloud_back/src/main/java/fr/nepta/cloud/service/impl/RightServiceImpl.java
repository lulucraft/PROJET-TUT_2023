package fr.nepta.cloud.service.impl;

import java.util.Collection;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.nepta.cloud.model.Right;
import fr.nepta.cloud.repository.RightRepo;
import fr.nepta.cloud.service.RightService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor @Slf4j
@Transactional
public class RightServiceImpl implements RightService {

	private final RightRepo rRepo;

	@Override
	public Right getRight(long rightId) {
		return rRepo.findById(rightId).get();
	}

	@Override
	public Right getRight(String name) {
		log.info("Fetching right from the database");
		return rRepo.findByName(name);
	}

	@Override
	public Right saveRight(Right right) {
		log.info("Saving new right to the database");
		return rRepo.save(right);
	}

	@Override
	public Collection<Right> getRights() {
		log.info("Fetching all rights");
		return rRepo.findAll();
	}

}
