package com.sistema_escolar.infra.exceptions;

public class EntityAlreadyExistsException extends RuntimeException {

    public EntityAlreadyExistsException(){
        super("Entidade já está cadastrada");
    }

    public EntityAlreadyExistsException(String message) {
        super(message);
    }
}
