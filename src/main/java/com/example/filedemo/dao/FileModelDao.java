package com.example.filedemo.dao;

import com.example.filedemo.model.FileModel;

import java.util.List;

public interface FileModelDao {

	List<String> findAllFileNames();

	List<FileModel> findAllFileModels();

	List<FileModel> findAllFileModelsFilterByName(String name);

	List<FileModel> findAllFileModelsFilterByType(String type);

	FileModel findById(int theId);

	void save(FileModel theEmployee);

	void deleteById(int theId);

}
