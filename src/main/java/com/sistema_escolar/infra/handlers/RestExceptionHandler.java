package com.sistema_escolar.infra.handlers;

import com.sistema_escolar.exceptions.*;
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

    @ExceptionHandler(EntityAlreadyExistsException.class)
    private ResponseEntity<ErrorMessage> userAlreadyExistsException(EntityAlreadyExistsException exception){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorMessage.builder()
                .timestamp(LocalDateTime.now())
                .title("Entidade já está cadastrada")
                .message(exception.getMessage())
                .httpStatus(HttpStatus.BAD_REQUEST)
                .status(HttpStatus.BAD_REQUEST.value())
                .build());
    }

    @ExceptionHandler(InvalidCodeException.class)
    private ResponseEntity<ErrorMessage> invalidCodeException(InvalidCodeException exception){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorMessage.builder()
                .timestamp(LocalDateTime.now())
                .title("Código inválido")
                .message(exception.getMessage())
                .httpStatus(HttpStatus.BAD_REQUEST)
                .status(HttpStatus.BAD_REQUEST.value())
                .build());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    private ResponseEntity<ErrorMessage> entityNotFoundException(EntityNotFoundException exception){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorMessage.builder()
                .timestamp(LocalDateTime.now())
                .title("Entidade não foi encontrada")
                .message(exception.getMessage())
                .httpStatus(HttpStatus.NOT_FOUND)
                .status(HttpStatus.NOT_FOUND.value())
                .build());
    }

    @ExceptionHandler(UserAlreadyBelongsToAnEntityException.class)
    private ResponseEntity<ErrorMessage> userAlreadyBelongsToAnEntityException(UserAlreadyBelongsToAnEntityException exception){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorMessage.builder()
                .timestamp(LocalDateTime.now())
                .title("Usuário já pertence a uma entidade")
                .message(exception.getMessage())
                .httpStatus(HttpStatus.BAD_REQUEST)
                .status(HttpStatus.BAD_REQUEST.value())
                .build());
    }

    @ExceptionHandler(EntityDoesntBelongToUserException.class)
    private ResponseEntity<ErrorMessage> entityDoesntBelongToUserException(EntityDoesntBelongToUserException exception){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorMessage.builder()
                .timestamp(LocalDateTime.now())
                .title("Entidade não pertence a esse usuário")
                .message(exception.getMessage())
                .httpStatus(HttpStatus.BAD_REQUEST)
                .status(HttpStatus.BAD_REQUEST.value())
                .build());
    }

    @ExceptionHandler(AccountWasntValidatedException.class)
    private ResponseEntity<ErrorMessage> accountWasntValidatedException(AccountWasntValidatedException exception){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorMessage.builder()
                .timestamp(LocalDateTime.now())
                .title("Conta não foi validada")
                .message(exception.getMessage())
                .httpStatus(HttpStatus.BAD_REQUEST)
                .status(HttpStatus.BAD_REQUEST.value())
                .build());
    }

    @ExceptionHandler(UserDoesntBelongException.class)
    private ResponseEntity<ErrorMessage> userDoesntBelongException(UserDoesntBelongException exception){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorMessage.builder()
                .timestamp(LocalDateTime.now())
                .title("Usuário não está vinculado a entidade")
                .message(exception.getMessage())
                .httpStatus(HttpStatus.BAD_REQUEST)
                .status(HttpStatus.BAD_REQUEST.value())
                .build());
    }

    @ExceptionHandler(QuestionErrorException.class)
    private ResponseEntity<ErrorMessage> questionErrorException(QuestionErrorException exception){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorMessage.builder()
                .timestamp(LocalDateTime.now())
                .title("Erro no tratamento da questão")
                .message(exception.getMessage())
                .httpStatus(HttpStatus.BAD_REQUEST)
                .status(HttpStatus.BAD_REQUEST.value())
                .build());
    }

    @ExceptionHandler(TestErrorException.class)
    private ResponseEntity<ErrorMessage> testErrorException(TestErrorException exception){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorMessage.builder()
                .timestamp(LocalDateTime.now())
                .title("Erro no tratamento da prova")
                .message(exception.getMessage())
                .httpStatus(HttpStatus.BAD_REQUEST)
                .status(HttpStatus.BAD_REQUEST.value())
                .build());
    }
}
