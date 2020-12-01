package com.example.filedemo.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageValidator {

	void validateFile(MultipartFile file);
}
