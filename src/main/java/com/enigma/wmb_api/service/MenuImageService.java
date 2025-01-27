package com.enigma.wmb_api.service;

import com.enigma.wmb_api.dto.response.FileDownloadResponse;
import com.enigma.wmb_api.entity.Menu;
import com.enigma.wmb_api.entity.MenuImage;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MenuImageService {
    MenuImage create(MultipartFile multipartFile, Menu menu);
    List<MenuImage> createBulk(List<MultipartFile> multipartFiles, Menu menu);
    MenuImage update(String imageId, MultipartFile multipartFile);
    void deleteById(String id);
    FileDownloadResponse downloadImage(String id);
}
