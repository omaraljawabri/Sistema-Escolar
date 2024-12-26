package com.sistema_escolar.infra.handlers;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorMessage {
    private LocalDateTime timestamp;
    private String title;
    private String message;
    private HttpStatus httpStatus;
    private int status;
}
