package com.sistema_escolar.infra.exceptions;

public class InvalidCodeException extends RuntimeException {

    public InvalidCodeException(){
      super("Código enviado é inválido!");
    }

    public InvalidCodeException(String message) {
        super(message);
    }
}
