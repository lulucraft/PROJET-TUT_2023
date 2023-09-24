package fr.nepta.cloud.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.nepta.cloud.model.Right;

public interface RightRepo extends JpaRepository<Right, Long> {

	Right findByName(String name);

}
