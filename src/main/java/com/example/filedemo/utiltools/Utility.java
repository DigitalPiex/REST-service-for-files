package com.example.filedemo.utiltools;

import com.example.filedemo.model.FileModel;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public interface Utility {
	ResponseEntity<Resource> getResourceResponseEntity(HttpServletRequest request, Resource resource);

	void renameFileAndChangeFileModelProperties(String fileName, FileModel tempFileModel);

	void setNewFileModelProperties(String fileName, FileModel tempFileModel, String type, String newFilePath);

	String getNewFilePath(String fileName, StringBuilder stringBuilder, String filePath, String type);

	default void renameAndRemove(String filePath, String newFilePath) {
		File file = new File(filePath);
		File file2 = new File(newFilePath);

		try {
			file.renameTo(file2);
			Files.move(Path.of(filePath), Path.of(newFilePath));
		} catch (IOException e) {
			e.getMessage();
		}
	}

	default String getFileType(FileModel tempFileModel) {
		String[] fileType = tempFileModel.getFileName().split("\\.");
		return fileType[fileType.length - 1];
	}
}
