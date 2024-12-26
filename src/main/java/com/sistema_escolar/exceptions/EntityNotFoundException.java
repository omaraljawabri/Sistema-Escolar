package com.sistema_escolar.exceptions;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(){
      super("Entidade não foi encontrada");
    }

    public EntityNotFoundException(String message) {
        super(message);
    }
}
