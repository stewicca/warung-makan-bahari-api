package com.enigma.wmb_api.controller;

import com.enigma.wmb_api.util.ResponseUtil;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
@Slf4j
public class ErrorController {

    @ExceptionHandler({ResponseStatusException.class})
    public ResponseEntity<?> handlingResponseStatusException(ResponseStatusException e) {
        log.error("Exception ResponseStatusException : {}", e.getReason());
        HttpStatusCode statusCode = e.getStatusCode();
        return ResponseUtil.buildResponse(HttpStatus.valueOf(statusCode.value()), e.getReason(), null);
    }

    @ExceptionHandler({DataIntegrityViolationException.class})
    public ResponseEntity<?> handlingDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.error("Exception DataIntegrityViolationException : {}", e.getMessage());

        String message = "";
        HttpStatus status = HttpStatus.CONFLICT;

        if (e.getCause() != null) {
            String causeMessage = e.getCause().getMessage();
            if (causeMessage.contains("duplicate key value")) {
                message = "Data already exist.";
            } else if (causeMessage.contains("cannot be null")) {
                message = "Data cannot be null.";
                status = HttpStatus.BAD_REQUEST;
            } else if (causeMessage.contains("foreign key constraint")) {
                message = "Data cannot be deleted because it is used by other data.";
                status = HttpStatus.BAD_REQUEST;
            } else if (causeMessage.contains("Duplicate entry")) {
                message = "Data already exist.";
            } else {
                message = "Unexpected error occurred";
                status = HttpStatus.INTERNAL_SERVER_ERROR;
            }
        }

        return ResponseUtil.buildResponse(status, message, null);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<?> handlingConstraintViolationException(ConstraintViolationException e) {
        log.error("Exception ConstraintViolationException : {}", e.getMessage());
        return ResponseUtil.buildResponse(HttpStatus.BAD_REQUEST, e.getMessage(), null);
    }

}
