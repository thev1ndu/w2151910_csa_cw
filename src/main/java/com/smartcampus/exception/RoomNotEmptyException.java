package com.smartcampus.exception;

/**
 *
 * @author thevinduw
 */
public class RoomNotEmptyException extends RuntimeException {
    public RoomNotEmptyException(String message) {
        super(message);
    }
}