package com.enigma.wmb_api.controller;

import com.enigma.wmb_api.constant.Constant;
import com.enigma.wmb_api.dto.request.UserRequest;
import com.enigma.wmb_api.dto.request.UserUpdatePasswordRequest;
import com.enigma.wmb_api.dto.response.CommonResponse;
import com.enigma.wmb_api.dto.response.UserResponse;
import com.enigma.wmb_api.service.UserService;
import com.enigma.wmb_api.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = Constant.USER_API)
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "User Management", description = "APIs for user management, including creating users, getting user info, and updating passwords")
public class UserController {
    private static class CommonResponseUserResponse {}

    private final UserService userService;

    @Operation(summary = "Create a new user",
            description = "Allows an admin to create a new user. The user details are passed in the request body.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User created successfully", content = @Content(schema = @Schema(implementation = CommonResponseUserResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Only admins can create users", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
            })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody UserRequest request) {
        UserResponse userResponse = userService.create(request);
        return ResponseUtil.buildResponse(HttpStatus.CREATED, Constant.SUCCESS_CREATE_USER, userResponse);
    }

    @Operation(summary = "Get authenticated user's info",
            description = "Retrieves information about the currently authenticated user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User information retrieved successfully", content = @Content(schema = @Schema(implementation = CommonResponseUserResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
            })
    @GetMapping("/me")
    public ResponseEntity<?> getSelfInfo() {
        UserResponse userResponse = userService.getAuthentication();
        return ResponseUtil.buildResponse(HttpStatus.OK, Constant.SUCCESS_GET_USER_INFO, userResponse);
    }

    @Operation(summary = "Update user password",
            description = "Allows a user to update their password by providing the current and new passwords. Admins can update any user's password.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Password updated successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
                    @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
            })
    @PatchMapping("/{id}/update-password")
    public ResponseEntity<?> updatePassword(@PathVariable String id, @RequestBody UserUpdatePasswordRequest request) {
        userService.updatePassword(id, request);
        return ResponseUtil.buildResponse(HttpStatus.OK, Constant.SUCCESS_UPDATE_PASSWORD, null);
    }
}

