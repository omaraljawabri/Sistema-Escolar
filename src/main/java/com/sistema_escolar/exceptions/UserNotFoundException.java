package com.sistema_escolar.exceptions;

public class UserNotFoundException extends RuntimeException{

    public UserNotFoundException() {
        super("Usuário não foi encontrado");
    }

    public UserNotFoundException(String message){
        super(message);
    }
}
