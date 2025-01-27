package com.enigma.wmb_api.service.impl;

import com.enigma.wmb_api.constant.Constant;
import com.enigma.wmb_api.dto.request.MenuRequest;
import com.enigma.wmb_api.dto.request.SearchMenuRequest;
import com.enigma.wmb_api.dto.response.FileResponse;
import com.enigma.wmb_api.dto.response.MenuResponse;
import com.enigma.wmb_api.entity.File;
import com.enigma.wmb_api.entity.Menu;
import com.enigma.wmb_api.entity.MenuImage;
import com.enigma.wmb_api.repository.MenuRepository;
import com.enigma.wmb_api.service.MenuImageService;
import com.enigma.wmb_api.service.MenuService;
import com.enigma.wmb_api.specification.MenuSpecification;
import com.enigma.wmb_api.util.SortUtil;
import com.enigma.wmb_api.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {
    private final MenuRepository menuRepository;
    private final MenuImageService menuImageService;
    private final ValidationUtil validationUtil;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public MenuResponse create(List<MultipartFile> multipartFiles, MenuRequest request) {
        validationUtil.validate(request);
        Menu menu = Menu.builder()
                .name(request.getName())
                .price(request.getPrice())
                .category(request.getCategory())
                .build();
        menuRepository.saveAndFlush(menu);
        if (multipartFiles != null && !multipartFiles.isEmpty()) {
            List<MenuImage> menuImages = menuImageService.createBulk(multipartFiles, menu);
            menu.setImages(menuImages);
        }
        return toMenuResponse(menu);
    }

    @Transactional(readOnly = true)
    @Override
    public MenuResponse getById(String id) {
        Menu menu = getOne(id);
        return toMenuResponse(menu);
    }

    @Transactional(readOnly = true)
    @Override
    public Menu getOne(String id) {
        Optional<Menu> optionalMenu = menuRepository.findById(id);

        if (optionalMenu.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "menu not found");
        }
        return optionalMenu.get();
    }

    @Transactional(readOnly = true)
    @Override
    public Page<MenuResponse> getAll(SearchMenuRequest request) {
        Sort sortBy = SortUtil.parseSort(request.getSortBy());
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sortBy);
        Specification<Menu> specification = MenuSpecification.getSpecification(request);
        Page<Menu> menuPage = menuRepository.findAll(specification, pageable);
        return menuPage.map(this::toMenuResponse);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public MenuResponse update(String id, List<MultipartFile> files, MenuRequest request) {
        Menu currentMenu = getOne(id);
        currentMenu.setName(request.getName());
        currentMenu.setPrice(request.getPrice());
        currentMenu.setCategory(request.getCategory());

        if (files != null && !files.isEmpty()) {
            List<MenuImage> menuImages = menuImageService.createBulk(files, currentMenu);
            if (currentMenu.getImages() != null) {
                currentMenu.getImages().addAll(menuImages);
            } else {
                currentMenu.setImages(menuImages);
            }
        }

        menuRepository.save(currentMenu);
        return toMenuResponse(currentMenu);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteById(String id) {
        Menu menu = getOne(id);
        if (menu.getImages() != null && !menu.getImages().isEmpty()) {
            menu.getImages().stream().map(File::getId)
                    .forEach(menuImageService::deleteById);
        }
        menuRepository.delete(menu);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public MenuResponse updateImage(MultipartFile file, String imageId) {
        MenuImage menuImage = menuImageService.update(imageId, file);
        return toMenuResponse(menuImage.getMenu());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteImage(String imageId) {
        menuImageService.deleteById(imageId);
    }

    private MenuResponse toMenuResponse(Menu menu) {
        List<FileResponse> images = menu.getImages() != null && !menu.getImages().isEmpty() ?
                menu.getImages().stream().map(file -> FileResponse.builder()
                        .id(file.getId())
                        .url(Constant.IMAGE_API + "/" + file.getId())
                        .build()).toList() :
                Collections.emptyList();

        return MenuResponse.builder()
                .id(menu.getId())
                .name(menu.getName())
                .price(menu.getPrice())
                .category(menu.getCategory())
                .images(images)
                .build();
    }
}
