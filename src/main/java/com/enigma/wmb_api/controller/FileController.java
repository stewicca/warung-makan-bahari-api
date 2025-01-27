package com.enigma.wmb_api.controller;

import com.enigma.wmb_api.constant.Constant;
import com.enigma.wmb_api.dto.response.CommonResponse;
import com.enigma.wmb_api.dto.response.FileDownloadResponse;
import com.enigma.wmb_api.service.MenuImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = Constant.API)
@Tag(name = "File Management", description = "APIs for downloading files, including images related to menu items")
public class FileController {

    private final MenuImageService menuImageService;

    @Operation(summary = "Download an image",
            description = "Download an image associated with a menu item by its ID. The image is returned as an inline file in the response body.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Image downloaded successfully", content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE)),
                    @ApiResponse(responseCode = "404", description = "Image not found", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
            })
    @GetMapping(path = "/images/{id}")
    public ResponseEntity<?> downloadImage(@PathVariable String id) {
        FileDownloadResponse response = menuImageService.downloadImage(id);
        String headerValue = String.format("inline; filename=%s", response.getResource().getFilename());
        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .contentType(MediaType.valueOf(response.getContentType()))
                .body(response.getResource());
    }

}
