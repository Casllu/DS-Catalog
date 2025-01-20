package com.lucasalmeida.dscatalog.servicies.exceptions;

public class EntityNotFoundException extends RuntimeException{

    public EntityNotFoundException(String msg) {
        super(msg);
    }
}
