package com.example.filedemo.restcontroller;

import com.example.filedemo.exception.FileStorageException;
import com.example.filedemo.model.FileModel;
import com.example.filedemo.property.FileStorageProperties;
import com.example.filedemo.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
public class FileController {

	private final FileStorageService fileStorageService;
	private Path fileStorageLocation;

	@Autowired
	public FileController(FileStorageService fileStorageService, FileStorageProperties fileStorageProperties) {
		this.fileStorageService = fileStorageService;
		this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
				.toAbsolutePath().normalize();

		try {
			Files.createDirectories(this.fileStorageLocation);
		} catch (Exception ex) {
			throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
		}
	}

	@GetMapping("/files/{fileId}")
	public ResponseEntity<Resource> downloadFile(@PathVariable int fileId, HttpServletRequest request) {

		FileModel tempFileModel = fileStorageService.findById(fileId);

		// Load file as Resource
		Resource resource = fileStorageService.loadFileAsResource(tempFileModel.getFileName());

		// Try to determine file's content type
		return fileStorageService.getResourceResponseEntity(request, resource);

	}

	@PostMapping("/files")
	public FileModel uploadFile(@RequestParam("file") MultipartFile file) {

		FileModel tempFileModel = fileStorageService.uploadFile(file);

		fileStorageService.saveMetaData(tempFileModel);

		return tempFileModel;
	}

	@PutMapping("/files/{fileId}")
	public FileModel changeFileMetaData(@PathVariable int fileId, @RequestParam String fileName) {

		FileModel tempFileModel = fileStorageService.findById(fileId);

		fileStorageService.renameFileAndChangeFileModelProperties(fileName, tempFileModel);

		fileStorageService.saveMetaData(tempFileModel);

		return tempFileModel;
	}

	@DeleteMapping("/files/{fileId}")
	public String deleteFile(@PathVariable int fileId) {

		FileModel tempFileModel = fileStorageService.findById(fileId);

		fileStorageService.deleteFile(fileId);

		return "File " + tempFileModel.getFileName() + " has been successfully deleted";
	}

	@GetMapping("/filenames")
	public List<String> getAllFileNames() {

		return fileStorageService.getAllNames();

	}

	@GetMapping("/filemodels")
	public List<FileModel> getAllFileModels() {

		return fileStorageService.getAllModels();

	}

	@GetMapping("/filemodels/byname")
	public List<FileModel> getAllFileModelsFilterByName(@RequestParam String filename) {

		return fileStorageService.getAllModelsFilterByName(filename);

	}

	//Use dd-MM-yyyy-dd-MM-yyyy format as requested param like 03-10-1999-15-06-2020
	@GetMapping("/filemodels/bydate")
	public List<FileModel> getAllFileModelsFilterByDate(@RequestParam String interval) {

		return fileStorageService.getAllModelsFilterByDate(interval);

	}

	//Use most common types like jpeg/doc/pdf
	@GetMapping("/filemodels/bytype")
	public List<FileModel> getAllFileModelsFilterByType(@RequestParam String type) {

		return fileStorageService.getAllModelsFilterByType(type);

	}

	//Use id string with dash-delimiters as requested param like 1-4-10-55-102
	@GetMapping("/multiplefiles")
	public byte[] downloadMultipleFilesAsSingleArchive(@RequestParam String filesId, HttpServletResponse response)
			throws IOException {

		return fileStorageService.downloadFilesAsSingleArchive(filesId, response);

	}


}
