package com.example.filedemo.service;

import com.example.filedemo.model.FileModel;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface FileStorage {

	FileModel uploadFile(MultipartFile file);

	Resource loadFileAsResource(String fileName);

	FileModel findById(int id);

	void saveMetaData(FileModel fileModel);

	void deleteFile(int id);

	List<String> getAllNames();

	List<FileModel> getAllModels();

	List<FileModel> getAllModelsFilterByName(String name);

	List<FileModel> getAllModelsFilterByDate(String date);

	List<FileModel> getAllModelsFilterByType(String type);

	byte[] downloadFilesAsSingleArchive(String filesId, HttpServletResponse response) throws IOException;

}
