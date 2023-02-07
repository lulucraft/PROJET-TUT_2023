package fr.nepta.intranet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.nepta.intranet.model.Newsletter;

@Repository
public interface NewsletterRepo extends JpaRepository<Newsletter, Long> {

}
