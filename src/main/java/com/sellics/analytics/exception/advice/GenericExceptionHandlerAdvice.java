package com.sellics.analytics.exception.advice;

import com.sellics.analytics.exception.GenericClientRuntimeException;
import com.sellics.analytics.exception.GenericServerRuntimeException;
import com.sellics.analytics.model.GenericExceptionResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;

/**
 * @author Sankar M <sankar.mm30@gmail.com>
 */
@RestController
@ControllerAdvice
public class GenericExceptionHandlerAdvice extends ResponseEntityExceptionHandler {

    /**
     * Handling client runtime exception
     *
     * @param exception the exception to handle
     * @param request the HttpServletRequest
     * @return the response entity
     */
    @ExceptionHandler(GenericClientRuntimeException.class)
    public final ResponseEntity<GenericExceptionResponseDto> handleClientRuntimeException(
            GenericClientRuntimeException exception, HttpServletRequest request) {

        return new ResponseEntity<>(
                GenericExceptionResponseDto.builder()
                        .timestamp(Date.from(Instant.now()))
                        .status(HttpStatus.BAD_REQUEST.value())
                        .errors(Collections.singletonList(exception.getMessage()))
                        .message(HttpStatus.BAD_REQUEST.getReasonPhrase())
                        .path(request.getContextPath() + request.getServletPath())
                        .build()
                , HttpStatus.BAD_REQUEST);
    }

    /**
     * Handling server runtime exception
     *
     * @param exception the exception to handle
     * @param request the HttpServletRequest
     * @return the response entity
     */
    @ExceptionHandler(GenericServerRuntimeException.class)
    public final ResponseEntity<GenericExceptionResponseDto> handleServerRuntimeException(
            GenericServerRuntimeException exception, HttpServletRequest request) {

        return new ResponseEntity<>(
                GenericExceptionResponseDto.builder()
                        .timestamp(Date.from(Instant.now()))
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .errors(Collections.singletonList(exception.getMessage()))
                        .message(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                        .path(request.getContextPath() + request.getServletPath())
                        .build()
                , HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
