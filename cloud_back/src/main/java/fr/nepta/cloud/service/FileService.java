package fr.nepta.cloud.service;

import java.util.Collection;

import fr.nepta.cloud.model.File;
import fr.nepta.cloud.model.User;

public interface FileService {

	File saveFile(File file);

	Collection<File> getFiles();

	File getFile(long id);

	File getFile(String string);

	User getFileOwner(long fileId);

}
