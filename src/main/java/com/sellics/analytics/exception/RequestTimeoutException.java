package com.sellics.analytics.exception;

/**
 * @author Sankar M <sankar.mm30@gmail.com>
 */
public class RequestTimeoutException extends GenericClientRuntimeException {

    public static final String MESSAGE = "Your request timed out. Please try again after sometime.";

    public RequestTimeoutException() {

        super(MESSAGE);
    }
}