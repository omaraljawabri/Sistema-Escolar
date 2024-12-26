package com.sistema_escolar.infra.exceptions;

public class TestErrorException extends RuntimeException {

    public TestErrorException(){
      super("Erro ao tratar prova");
    }

    public TestErrorException(String message) {
        super(message);
    }
}
