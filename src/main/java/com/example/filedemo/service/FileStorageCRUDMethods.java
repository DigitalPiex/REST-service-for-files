package com.example.filedemo.service;

import com.example.filedemo.model.FileModel;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;


public interface FileStorageCRUDMethods {

	FileModel uploadFile(MultipartFile file);

	Resource loadFileAsResource(String fileName);

	FileModel findById(int id);

	void saveMetaData(FileModel fileModel);

	void deleteFile(int id);


}
