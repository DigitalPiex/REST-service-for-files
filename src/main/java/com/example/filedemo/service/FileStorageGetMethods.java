package com.example.filedemo.service;

import com.example.filedemo.model.FileModel;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface FileStorageGetMethods {

	List<String> getAllNames();

	List<FileModel> getAllModels();

	List<FileModel> getAllModelsFilterByName(String name);

	List<FileModel> getAllModelsFilterByDate(String date);

	List<FileModel> getAllModelsFilterByType(String type);

	byte[] downloadFilesAsSingleArchive(String filesId, HttpServletResponse response) throws IOException;
}
