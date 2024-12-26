package com.sistema_escolar.infra.exceptions;

public class QuestionErrorException extends RuntimeException {

    public QuestionErrorException(){
      super("Um erro aconteceu ao tratar questão");
    }

    public QuestionErrorException(String message) {
        super(message);
    }
}
