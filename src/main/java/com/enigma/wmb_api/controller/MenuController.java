package com.enigma.wmb_api.controller;

import com.enigma.wmb_api.constant.Constant;
import com.enigma.wmb_api.dto.request.MenuRequest;
import com.enigma.wmb_api.dto.request.SearchMenuRequest;
import com.enigma.wmb_api.dto.response.CommonResponse;
import com.enigma.wmb_api.dto.response.MenuResponse;
import com.enigma.wmb_api.service.MenuService;
import com.enigma.wmb_api.util.ResponseUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.enigma.wmb_api.constant.Constant.*;

@RestController
@RequestMapping(path = Constant.MENU_API)
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Menu Management", description = "Operations related to managing menus")
public class MenuController {

    private static class CommonResponseMenuResponse extends CommonResponse<MenuResponse> {
    }

    private static class CommonResponseListMenuResponse extends CommonResponse<List<MenuResponse>> {
    }

    private final MenuService menuService;
    private final ObjectMapper objectMapper;

    @Operation(summary = "Create a new menu",
            description = "This endpoint allows an admin to create a new menu item. Images can be optionally uploaded with the menu details.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Menu created successfully", content = @Content(schema = @Schema(implementation = CommonResponseMenuResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
                    @ApiResponse(responseCode = "403", description = "Forbidden access", content = @Content(schema = @Schema(implementation = CommonResponse.class)))
            })
    @PreAuthorize("hasAnyRole('ADMIN', 'CASHIER')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createNewMenu(
            @Parameter(content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)) @RequestPart(name = "images", required = false) List<MultipartFile> multipartFiles,
            @RequestPart(name = "menu") String menu
    ) {
        try {
            MenuRequest request = objectMapper.readValue(menu, MenuRequest.class);
            MenuResponse savedMenu = menuService.create(multipartFiles, request);
            return ResponseUtil.buildResponse(HttpStatus.CREATED, SUCCESS_CREATE_MENU, savedMenu);
        } catch (Exception e) {
            return ResponseUtil.buildResponse(HttpStatus.BAD_REQUEST, e.getMessage(), null);
        }
    }

    @Operation(summary = "Retrieve all menus",
            description = "This endpoint retrieves a paginated list of all available menus, with optional filtering and sorting.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Menus retrieved successfully", content = @Content(schema = @Schema(implementation = CommonResponseListMenuResponse.class))),
            })
    @GetMapping
    public ResponseEntity<?> getAllMenu(
            @RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "10") Integer size,
            @RequestParam(name = "sortBy", required = false) String sortBy,
            @RequestParam(name = "q", required = false) String query,
            @RequestParam(name = "minPrice", required = false) Long minPrice,
            @RequestParam(name = "maxPrice", required = false) Long maxPrice
    ) {
        SearchMenuRequest pagingAndSortingRequest = SearchMenuRequest.builder()
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .query(query)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .build();
        Page<MenuResponse> menus = menuService.getAll(pagingAndSortingRequest);
        return ResponseUtil.buildResponsePage(HttpStatus.OK, SUCCESS_GET_ALL_MENU, menus);
    }

    @Operation(summary = "Get menu by ID",
            description = "Retrieve details of a specific menu item by its ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Menu details retrieved successfully", content = @Content(schema = @Schema(implementation = CommonResponseMenuResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Menu not found", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
            })
    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        MenuResponse menu = menuService.getById(id);
        return ResponseUtil.buildResponse(HttpStatus.OK, SUCCESS_GET_MENU_BY_ID, menu);
    }

    @Operation(summary = "Update menu",
            description = "Update the details of an existing menu item by its ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Menu updated successfully", content = @Content(schema = @Schema(implementation = CommonResponseMenuResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
                    @ApiResponse(responseCode = "403", description = "Forbidden access", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Menu not found", content = @Content(schema = @Schema(implementation = CommonResponse.class)))
            })
    @PutMapping(path = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CASHIER')")
    public ResponseEntity<?> update(
            @PathVariable String id,
            @Parameter(content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)) @RequestPart(name = "images", required = false) List<MultipartFile> multipartFiles,
            @RequestPart(name = "menu") String menu
    ) {
        try {
            MenuRequest request = objectMapper.readValue(menu, MenuRequest.class);
            MenuResponse savedMenu = menuService.update(id, multipartFiles, request);
            return ResponseUtil.buildResponse(HttpStatus.CREATED, SUCCESS_UPDATE_MENU, savedMenu);
        } catch (Exception e) {
            return ResponseUtil.buildResponse(HttpStatus.BAD_REQUEST, e.getMessage(), null);
        }
    }

    @Operation(summary = "Delete menu",
            description = "Delete a specific menu item by its ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Menu deleted successfully", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
                    @ApiResponse(responseCode = "403", description = "Forbidden access", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Menu not found", content = @Content(schema = @Schema(implementation = CommonResponse.class)))
            })
    @PreAuthorize("hasAnyRole('ADMIN', 'CASHIER')")
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteById(@PathVariable String id) {
        menuService.deleteById(id);
        return ResponseUtil.buildResponse(HttpStatus.OK, SUCCESS_DELETE_MENU, null);
    }

    @Operation(summary = "Update specific menu image",
            description = "Updates a specific image associated with a menu. Requires image ID and the new image file to be uploaded.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Menu image updated successfully", content = @Content(schema = @Schema(implementation = CommonResponseMenuResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
                    @ApiResponse(responseCode = "403", description = "Forbidden access", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Menu image not found", content = @Content(schema = @Schema(implementation = CommonResponse.class)))
            })
    @PreAuthorize("hasAnyRole('ADMIN', 'CASHIER')")
    @PatchMapping(path = "/images/{id}")
    public ResponseEntity<?> updateSpecifiedImageById(
            @PathVariable String id,
            @RequestParam(name = "image") MultipartFile file
    ) {
        MenuResponse menuResponse = menuService.updateImage(file, id);
        return ResponseUtil.buildResponse(HttpStatus.OK, "Successfully updated menu image", menuResponse);
    }

    @Operation(summary = "Delete specific menu image",
            description = "Deletes a specific image associated with a menu using the image ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Menu image deleted successfully", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
                    @ApiResponse(responseCode = "403", description = "Forbidden access", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Menu image not found", content = @Content(schema = @Schema(implementation = CommonResponse.class)))
            })
    @PreAuthorize("hasAnyRole('ADMIN', 'CASHIER')")
    @DeleteMapping(path = "/images/{id}")
    public ResponseEntity<?> deleteSpecifiedImageById(@PathVariable String id) {
        menuService.deleteImage(id);
        return ResponseUtil.buildResponse(HttpStatus.OK, "Successfully deleted menu image", null);
    }

}

