package org.example.exception;

public class InvalidPacketException extends RuntimeException{

    public InvalidPacketException(String msg){
        super(msg);
    }
}
