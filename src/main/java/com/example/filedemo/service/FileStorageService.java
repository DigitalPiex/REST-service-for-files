package com.example.filedemo.service;

import com.example.filedemo.dao.FileModelDao;
import com.example.filedemo.exception.FileStorageException;
import com.example.filedemo.exception.MyFileNotFoundException;
import com.example.filedemo.exception.ValidationException;
import com.example.filedemo.model.FileModel;
import com.example.filedemo.property.FileStorageProperties;
import com.example.filedemo.utiltools.Utility;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class FileStorageService implements FileStorageCRUDMethods, FileStorageGetMethods, FileStorageValidator {

	private Utility utilityManager;
	private FileModelDao fileModelDao;
	private Path fileStorageLocation;
	private static final Calendar CALENDAR = new GregorianCalendar();
	private static final SimpleDateFormat DATE_FORMAT_FOR_MYSQL_TABLE = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static Logger logger = LoggerFactory.getLogger(FileStorageService.class);

	@Autowired
	public FileStorageService(FileModelDao fileModelDao, FileStorageProperties fileStorageProperties, Utility utilityManager) {
		this.fileModelDao = fileModelDao;
		this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
				.toAbsolutePath().normalize();
		this.utilityManager = utilityManager;

		try {
			Files.createDirectories(this.fileStorageLocation);
		} catch (Exception ex) {
			throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
		}
	}

	@Override
	public FileModel uploadFile(MultipartFile file) {

		validateFile(file);

		// Normalize file name
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());

		try {
			// Check if the file's name contains invalid characters
			if (fileName.contains("..")) {
				throw new FileStorageException("Filename contains invalid path sequence " + fileName);
			}

			// Copy file to the target location (Replacing existing file with the same name)
			Path targetLocation = this.fileStorageLocation.resolve(fileName);

			String filePath = String.valueOf(this.fileStorageLocation.resolve(fileName));

			Date date = CALENDAR.getTime();

			FileModel uploadedFile = new FileModel(DATE_FORMAT_FOR_MYSQL_TABLE.format(date),
					DATE_FORMAT_FOR_MYSQL_TABLE.format(date),
					fileName, file.getContentType(), file.getSize(), filePath, null);

			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

			fileModelDao.save(uploadedFile);

			String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
					.path("/files/")
					.path(String.valueOf(uploadedFile.getId()))
					.toUriString();

			uploadedFile.setFileDownloadUri(fileDownloadUri);

			fileModelDao.save(uploadedFile);

			return uploadedFile;

		} catch (IOException ex) {
			throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
		}
	}


	@Override
	public Resource loadFileAsResource(String fileName) {
		try {

			Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
			Resource resource = new UrlResource(filePath.toUri());

			if (resource.exists()) {
				return resource;
			} else {
				throw new MyFileNotFoundException("File not found " + fileName);
			}
		} catch (MalformedURLException ex) {
			throw new MyFileNotFoundException("File not found " + fileName, ex);
		}
	}

	@Override
	@Transactional
	public FileModel findById(int id) {
		return fileModelDao.findById(id);
	}

	@Override
	@Transactional
	public void saveMetaData(FileModel fileModel) {

		fileModelDao.save(fileModel);
	}

	@Override
	@Transactional
	public void deleteFile(int id) {

		FileModel fileModel = fileModelDao.findById(id);

		try {
			File file = new File(fileModel.getFilePath());
			if (file.delete()) logger.info(fileModel.getFileName() + " has been successfully deleted");
		} catch (Exception e) {
			throw new FileStorageException("File does not exist");
		}

		fileModelDao.deleteById(id);

	}

	@Override
	@Transactional
	public List<String> getAllNames() {

		return fileModelDao.findAllFileNames();
	}

	@Override
	@Transactional
	public List<FileModel> getAllModels() {

		return fileModelDao.findAllFileModels();
	}

	@Override
	@Transactional
	public List<FileModel> getAllModelsFilterByName(String name) {

		return fileModelDao.findAllFileModelsFilterByName(name);

	}

	@Override
	@Transactional
	public List<FileModel> getAllModelsFilterByType(String type) {

		return fileModelDao.findAllFileModelsFilterByType(type);

	}

	@Override
	@Transactional
	public List<FileModel> getAllModelsFilterByDate(String interval) {

		List<FileModel> models = fileModelDao.findAllFileModels();

		SimpleDateFormat tempFormat = new SimpleDateFormat("dd-MM-yyyy");

		Date beginDate = null;
		Date endDate = null;

		try {
			beginDate = tempFormat.parse(interval.substring(0, 10));
			endDate = tempFormat.parse(interval.substring(11, 21));
			logger.info(beginDate.getTime() + " " + endDate.getTime());
		} catch (ParseException e) {
			e.getMessage();
		}

		List<FileModel> returnList = new ArrayList<>();

		for (FileModel fileModel : models) {

			Date tempDate = null;

			try {
				tempDate = FileStorageService.DATE_FORMAT_FOR_MYSQL_TABLE.parse(fileModel.getChangeDate());
				logger.info(String.valueOf(tempDate.getTime()));
			} catch (ParseException e) {
				e.getMessage();
			}

			if (tempDate.after(beginDate) && tempDate.before(endDate)) returnList.add(fileModel);
		}

		return returnList;


	}

	@Override
	public void validateFile(MultipartFile file) {

		if (file.getSize() > 15_000_000L)
			throw new ValidationException("File size must be less than 15 MB");

		if (!((file.getOriginalFilename().endsWith(".pdf") ||
				file.getOriginalFilename().endsWith(".txt") ||
				file.getOriginalFilename().endsWith(".png") ||
				file.getOriginalFilename().endsWith(".gif") ||
				file.getOriginalFilename().endsWith(".jpg") ||
				file.getOriginalFilename().endsWith(".jpeg") ||
				file.getOriginalFilename().endsWith(".doc") ||
				file.getOriginalFilename().endsWith(".docx"))))
			throw new ValidationException("This type of file is not supported: " + file.getContentType());
	}


	@Override
	@Transactional
	public byte[] downloadFilesAsSingleArchive(String filesId, HttpServletResponse response) throws IOException {

		//setting headers
		response.setContentType("application/zip");
		response.setStatus(HttpServletResponse.SC_OK);
		response.addHeader("Content-Disposition", "attachment; filename=\"Files.zip\"");

		//creating byteArray stream, make it buffered and passing this buffer to ZipOutputStream
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(byteArrayOutputStream);
		ZipOutputStream zipOutputStream = new ZipOutputStream(bufferedOutputStream);

		//packing files
		String[] allFilesId = filesId.split("-");
		for (String idString : allFilesId) {

			int tempFileId = Integer.parseInt(idString);

			FileModel tempFileModel = fileModelDao.findById(tempFileId);
			BufferedInputStream fis = new BufferedInputStream(new FileInputStream(tempFileModel.getFilePath()));

			ZipEntry tempEntry = new ZipEntry(tempFileModel.getFileName());
			zipOutputStream.putNextEntry(tempEntry);
			IOUtils.copy(fis, zipOutputStream);

			fis.close();
			zipOutputStream.closeEntry();

		}

		zipOutputStream.finish();
		zipOutputStream.flush();

		IOUtils.closeQuietly(zipOutputStream);
		IOUtils.closeQuietly(bufferedOutputStream);
		IOUtils.closeQuietly(byteArrayOutputStream);

		return byteArrayOutputStream.toByteArray();
	}

	public ResponseEntity<Resource> getResourceResponseEntity(HttpServletRequest request, Resource resource) {
		return utilityManager.getResourceResponseEntity(request, resource);
	}

	public void renameFileAndChangeFileModelProperties(String fileName, FileModel tempFileModel) {

		utilityManager.renameFileAndChangeFileModelProperties(fileName, tempFileModel);
	}

}


