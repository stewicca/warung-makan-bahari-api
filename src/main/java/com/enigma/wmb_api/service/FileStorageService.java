package com.enigma.wmb_api.service;

import com.enigma.wmb_api.constant.FileType;
import com.enigma.wmb_api.dto.response.FileInfo;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileStorageService {
    FileInfo storeFile(FileType fileType, String prefixDirectory, MultipartFile multipartFile, List<String> contentTypes);
    Resource readFile(String path);
    void deleteFile(String path);
}
