package com.sistema_escolar.exceptions;

public class UserAlreadyBelongsToAnEntityException extends RuntimeException {

    public UserAlreadyBelongsToAnEntityException(){
      super("Usuário já pertence a uma entidade");
    }

    public UserAlreadyBelongsToAnEntityException(String message) {
        super(message);
    }
}
