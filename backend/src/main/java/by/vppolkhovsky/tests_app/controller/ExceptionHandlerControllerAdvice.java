package by.vppolkhovsky.tests_app.controller;

import by.vppolkhovsky.tests_app.exception.JwtRequiredException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.net.URI;

@ControllerAdvice
public class ExceptionHandlerControllerAdvice {

    @ExceptionHandler(JwtRequiredException.class)
    public ResponseEntity<Void> handleJwtRequiredException() {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create("/login-user"));

        return ResponseEntity.status(HttpStatus.SEE_OTHER)
            .headers(httpHeaders)
            .build();
    }
}
