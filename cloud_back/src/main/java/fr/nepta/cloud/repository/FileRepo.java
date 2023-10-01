package fr.nepta.cloud.repository;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import fr.nepta.cloud.model.File;
import fr.nepta.cloud.model.User;

public interface FileRepo extends JpaRepository<File, Long> {

	File findByName(String name);

	Collection<File> findAllByArchived(boolean archived);

	@Query("SELECT u FROM user u JOIN u.files uf JOIN file f ON uf.id = f.id WHERE f.id = :fileId")// JOIN user_files uf ON uf.user_id = u.id AND files_id = f.id
	User findFileOwner(long fileId);

}
