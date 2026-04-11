package com.smartcampus.exception;

/**
 *
 * @author thevinduw
 */
public class SensorUnavailableException extends RuntimeException {
    public SensorUnavailableException(String message) {
        super(message);
    }
}