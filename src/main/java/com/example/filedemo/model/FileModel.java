package com.example.filedemo.model;

import javax.persistence.*;

@Entity
@Table(name = "file_model")
public class FileModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

	@Column(name = "load_date")
	private String loadDate;

	@Column(name = "change_date")
	private String changeDate;

	@Column(name = "file_name")
	private String fileName;

	@Column(name = "file_type")
	private String fileType;

	@Column(name = "file_size")
	private long fileSize;

	@Column(name = "file_path")
	private String filePath;

	@Column(name = "file_download_uri")
	private String fileDownloadUri;

	public FileModel() {

	}

	public FileModel(String loadDate, String changeDate, String fileName, String fileType,
	                 long fileSize, String filePath, String fileDownloadUri) {
		this.loadDate = loadDate;
		this.changeDate = changeDate;
		this.fileName = fileName;
		this.fileType = fileType;
		this.fileSize = fileSize;
		this.filePath = filePath;
		this.fileDownloadUri = fileDownloadUri;
	}

	public long getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLoadDate() {
		return loadDate;
	}

	public void setLoadDate(String loadDate) {
		this.loadDate = loadDate;
	}

	public String getChangeDate() {
		return changeDate;
	}

	public void setChangeDate(String changeDate) {
		this.changeDate = changeDate;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long size) {
		this.fileSize = size;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getFileDownloadUri() {
		return fileDownloadUri;
	}

	public void setFileDownloadUri(String fileDownloadUri) {
		this.fileDownloadUri = fileDownloadUri;
	}

	@Override
	public String toString() {
		return "FileModel{" +
				"loadDate=" + loadDate +
				", fileName='" + fileName + '\'' +
				", fileType='" + fileType + '\'' +
				", size=" + fileSize + " byte" +
				", fileDownloadUri='" + fileDownloadUri + '\'' +
				'}';
	}
}
