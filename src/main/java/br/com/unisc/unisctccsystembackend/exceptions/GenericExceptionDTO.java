package br.com.unisc.unisctccsystembackend.exceptions;


import org.springframework.http.HttpStatus;

public record GenericExceptionDTO (
        String message,
        HttpStatus status) {
}
