package com.enigma.wmb_api.service;

import com.enigma.wmb_api.dto.request.MenuRequest;
import com.enigma.wmb_api.dto.request.SearchMenuRequest;
import com.enigma.wmb_api.dto.response.MenuResponse;
import com.enigma.wmb_api.entity.Menu;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MenuService {
    MenuResponse create(List<MultipartFile> multipartFiles, MenuRequest request);
    MenuResponse getById(String id);
    Menu getOne(String id);
    Page<MenuResponse> getAll(SearchMenuRequest request);
    MenuResponse update(String id, List<MultipartFile> files, MenuRequest request);
    void deleteById(String id);

    MenuResponse updateImage(MultipartFile file, String imageId);
    void deleteImage(String imageId);
}
