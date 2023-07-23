package fr.nepta.cloud.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.nepta.cloud.model.File;

public interface FileRepo extends JpaRepository<File, Long> {
}
