package com.example.filedemo.utiltools;

import com.example.filedemo.model.FileModel;
import com.example.filedemo.service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class UtilityImpl implements Utility {

	private static final SimpleDateFormat DATE_FORMAT_FOR_MYSQL_TABLE = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static Logger logger = LoggerFactory.getLogger(FileStorageService.class);

	@Override
	public ResponseEntity<Resource> getResourceResponseEntity(HttpServletRequest request, Resource resource) {
		String contentType = null;
		try {
			contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
		} catch (IOException ex) {
			logger.info("Could not determine file type.");
		}

		// Fallback to the default content type if type could not be determined
		if (contentType == null) {
			contentType = "application/octet-stream";
		}

		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}

	@Override
	public void renameFileAndChangeFileModelProperties(String fileName, FileModel tempFileModel) {

		StringBuilder stringBuilder = new StringBuilder();

		String filePath = tempFileModel.getFilePath();

		String type = getFileType(tempFileModel);

		String newFilePath = getNewFilePath(fileName, stringBuilder, filePath, type);

		setNewFileModelProperties(fileName, tempFileModel, type, newFilePath);

		renameAndRemove(filePath, newFilePath);
	}

	@Override
	public void setNewFileModelProperties(String fileName, FileModel tempFileModel, String type, String newFilePath) {

		Date date = new Date();

		tempFileModel.setFileName(fileName + "." + type);
		tempFileModel.setChangeDate(DATE_FORMAT_FOR_MYSQL_TABLE.format(date));
		tempFileModel.setFilePath(newFilePath);
	}

	@Override
	public String getNewFilePath(String fileName, StringBuilder stringBuilder, String filePath, String type) {
		String[] oldPath = filePath.split("\\\\");
		for (int i = 0; i < oldPath.length - 1; i++) {
			stringBuilder.append(oldPath[i]).append("\\");
		}
		stringBuilder.append(fileName).append(".").append(type);
		return stringBuilder.toString();
	}

}
