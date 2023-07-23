package fr.nepta.cloud.service;

import java.util.Collection;

import fr.nepta.cloud.model.File;

public interface FileService {

	File saveFile(File file);

	Collection<File> getFiles();

	File getFile(String fileName);

}
