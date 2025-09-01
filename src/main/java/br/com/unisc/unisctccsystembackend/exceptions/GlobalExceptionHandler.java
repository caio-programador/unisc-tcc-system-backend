package br.com.unisc.unisctccsystembackend.exceptions;

import jakarta.persistence.EntityNotFoundException;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<GenericExceptionDTO> handleEntityNotFoundException(EntityNotFoundException ex) {
        GenericExceptionDTO errorResponse = new GenericExceptionDTO(ex.getMessage(), HttpStatus.NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<GenericExceptionDTO> handleBadRequestException(BadRequestException ex) {
        GenericExceptionDTO errorResponse = new GenericExceptionDTO(ex.getMessage(), HttpStatus.BAD_REQUEST);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<GenericExceptionDTO> handleAccessDenied(AccessDeniedException ex) {
        GenericExceptionDTO errorResponse = new GenericExceptionDTO(ex.getMessage(), HttpStatus.FORBIDDEN);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<GenericExceptionDTO> handleAuth(AuthenticationException ex) {
        GenericExceptionDTO errorResponse = new GenericExceptionDTO(ex.getMessage(), HttpStatus.UNAUTHORIZED);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GenericExceptionDTO> handleException(Exception ex) {
        GenericExceptionDTO errorResponse = new GenericExceptionDTO("An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
