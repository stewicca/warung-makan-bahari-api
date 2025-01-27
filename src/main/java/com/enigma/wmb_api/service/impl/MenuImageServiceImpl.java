package com.enigma.wmb_api.service.impl;

import com.enigma.wmb_api.constant.FileType;
import com.enigma.wmb_api.dto.response.FileDownloadResponse;
import com.enigma.wmb_api.dto.response.FileInfo;
import com.enigma.wmb_api.entity.Menu;
import com.enigma.wmb_api.entity.MenuImage;
import com.enigma.wmb_api.repository.MenuImageRepository;
import com.enigma.wmb_api.service.FileStorageService;
import com.enigma.wmb_api.service.MenuImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuImageServiceImpl implements MenuImageService {
    private final MenuImageRepository menuImageRepository;
    private final FileStorageService fileStorageService;
    private final List<String> contentTypes = List.of("image/jpg", "image/png", "image/webp", "image/jpeg");
    private final String MENU = "menu";

    @Transactional(rollbackFor = Exception.class)
    @Override
    public MenuImage create(MultipartFile multipartFile, Menu menu) {
        FileInfo fileInfo = fileStorageService.storeFile(FileType.IMAGE, MENU, multipartFile, contentTypes);
        MenuImage menuImage = MenuImage.builder()
                .filename(fileInfo.getFilename())
                .contentType(multipartFile.getContentType())
                .size(multipartFile.getSize())
                .path(fileInfo.getPath())
                .menu(menu)
                .build();
        return menuImageRepository.saveAndFlush(menuImage);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<MenuImage> createBulk(List<MultipartFile> multipartFiles, Menu menu) {
        return multipartFiles.stream().map(multipartFile -> create(multipartFile, menu)).toList();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public MenuImage update(String imageId, MultipartFile multipartFile) {
        MenuImage menuImage = findByIdOrThrowNotFound(imageId);
        FileInfo fileInfo = fileStorageService.storeFile(FileType.IMAGE, MENU, multipartFile, contentTypes);
        fileStorageService.deleteFile(menuImage.getPath());
        menuImage.setPath(fileInfo.getPath());
        menuImageRepository.saveAndFlush(menuImage);
        return menuImage;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteById(String id) {
        MenuImage menuImage = findByIdOrThrowNotFound(id);
        String path = menuImage.getPath();
        menuImageRepository.delete(menuImage);
        fileStorageService.deleteFile(path);
    }

    @Transactional(readOnly = true)
    @Override
    public FileDownloadResponse downloadImage(String id) {
        MenuImage menuImage = menuImageRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "image not found"));
        Resource resource = fileStorageService.readFile(menuImage.getPath());
        return FileDownloadResponse.builder()
                .resource(resource)
                .contentType(menuImage.getContentType())
                .build();
    }

    @Transactional(readOnly = true)
    public MenuImage findByIdOrThrowNotFound(String id) {
        return menuImageRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "image not found"));
    }
}
