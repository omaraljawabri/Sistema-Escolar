package com.sistema_escolar.exceptions;

public class AccountWasntValidatedException extends RuntimeException {

    public AccountWasntValidatedException(){
      super("Conta não foi validada");
    }

    public AccountWasntValidatedException(String message) {
        super(message);
    }
}
