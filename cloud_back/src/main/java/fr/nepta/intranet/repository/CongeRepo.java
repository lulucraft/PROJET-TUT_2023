package fr.nepta.intranet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.nepta.intranet.model.Conge;

@Repository
public interface CongeRepo extends JpaRepository<Conge, Long> {

	Conge findById(long id);

}
