package com.sistema_escolar.infra.handlers;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "Momento em que o erro ocorreu", example = "2024-12-27T12:03:30", type = "Date")
    private LocalDateTime timestamp;
    @Schema(description = "Título do erro", example = "Usuário não encontrado", type = "String")
    private String title;
    @Schema(description = "Mensagem lançada pela excessão", example = "E-mail não foi encontrado", type = "String")
    private String message;
    @Schema(description = "Status HTTP lançado", example = "BAD_REQUEST", type = "HttpStatus")
    private HttpStatus httpStatus;
    @Schema(description = "Valor do status que foi lançado", example = "400", type = "int")
    private int status;
}
