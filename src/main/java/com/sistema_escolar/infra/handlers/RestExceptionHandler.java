package com.sistema_escolar.infra.handlers;

import com.sistema_escolar.infra.exceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    private ResponseEntity<ErrorMessage> userNotFoundHandler(UserNotFoundException exception){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorMessage.builder()
                .timestamp(LocalDateTime.now())
                .title("Usuário não encontrado")
                .message(exception.getMessage())
                .httpStatus(HttpStatus.NOT_FOUND)
                .status(HttpStatus.NOT_FOUND.value())
                .build());
    }
}
