package com.sistema_escolar.utils;

import java.security.SecureRandom;

public class CodeGenerator {

    public static String generateCode(){
        String pattern = "8CTJOQKXH4@F1NZ&GDLIW6!3V2%#R7A90E5BP?YSMU ";
        SecureRandom random = new SecureRandom();
        StringBuffer stringBuffer = new StringBuffer(11);
        for (int i = 0; i < 11; i++) {
            stringBuffer.append(pattern.charAt(random.nextInt(42)));
        }
        return stringBuffer.toString();
    }

}
