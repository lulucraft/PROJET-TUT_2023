package fr.nepta.cloud.service.impl;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.nepta.cloud.model.File;
import fr.nepta.cloud.repository.FileRepo;
import fr.nepta.cloud.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor @Slf4j
@Transactional
public class FileServiceImpl implements FileService {

	@Autowired
	private FileRepo fileRepo;

	@Override
	public File saveFile(File file) {
		log.info("Saving file to the database");
		return fileRepo.save(file);
	}

	@Override
	public Collection<File> getFiles() {
		log.info("Fetching files from the database");
		return fileRepo.findAll();
	}

	@Override
	public File getFile(long id) {
		log.info("Fetching file '{}'", id);
		return fileRepo.findById(id).get();
	}
	
	@Override
	public File getFile(String name) {
		log.info("Fetching file '{}'", name);
		return fileRepo.findByName(name);
	}

}
