package com.sistema_escolar.infra.exceptions;

public class UserDoesntBelongException extends RuntimeException {

    public UserDoesntBelongException(){
      super("Usuário não está vinculado a entidade");
    }

    public UserDoesntBelongException(String message) {
        super(message);
    }
}
