package com.sistema_escolar.infra.exceptions;

public class EntityDoesntBelongToUserException extends RuntimeException {

    public EntityDoesntBelongToUserException(){
        super("Entidade não pertence a esse usuário");
    }

    public EntityDoesntBelongToUserException(String message) {
        super(message);
    }
}
