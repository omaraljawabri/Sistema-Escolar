package com.sistema_escolar.enums;

public enum UserRole {
    ADMIN("admin"),
    PROFESSOR("professor"),
    ESTUDANTE("estudante");

    private String role;

    UserRole(String role){
        this.role = role;
    }

    public String getRole(){
        return role;
    }
}
