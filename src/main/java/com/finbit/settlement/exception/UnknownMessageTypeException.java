package com.finbit.settlement.exception;

/**
 * Thrown when a feed message contains an unrecognized message type.
 */
public class UnknownMessageTypeException extends RuntimeException {

    public UnknownMessageTypeException(String messageType) {
        super("Unknown message type: " + messageType);
    }
}
