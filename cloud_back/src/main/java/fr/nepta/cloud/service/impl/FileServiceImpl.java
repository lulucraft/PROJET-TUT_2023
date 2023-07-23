package fr.nepta.cloud.service.impl;

import java.util.Collection;

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

	private final FileRepo fileRepo;

	@Override
	public File saveFile(File file) {
		log.info("Saving Newsletter to the database");
		return fileRepo.save(file);
	}

	@Override
	public Collection<File> getFiles() {
		log.info("Fetching Newsletters from the database");
		return fileRepo.findAll();
	}

	@Override
	public File getFile(String fileName) {
		for (File f : fileRepo.findAll()) {
			if (f.getName().equalsIgnoreCase(fileName)) {
				return f;
			}
		}

		log.info("Fetching file '{}'", fileName);
		return null;
	}

}
