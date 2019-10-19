package com.upgrad.quora.api.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.upgrad.quora.api.model.ErrorResponse;
import com.upgrad.quora.service.common.GenericErrorCode;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;

@ControllerAdvice
public class RestExceptionHandler {


    @ExceptionHandler(AuthorizationFailedException.class)
    public ResponseEntity<ErrorResponse> resourceNotFoundException(AuthorizationFailedException exe, WebRequest request){


        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(exe.getCode()).message(exe.getErrorMessage()), HttpStatus.NOT_FOUND
        );

    }

    @ExceptionHandler(InvalidQuestionException.class)
    public ResponseEntity<ErrorResponse> InvalidQuestionException(InvalidQuestionException exe, WebRequest request){


        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(exe.getCode()).message(exe.getErrorMessage()), HttpStatus.NOT_FOUND
        );

    }


    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> UserNotFoundException(UserNotFoundException exe, WebRequest request){


        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(exe.getCode()).message(exe.getErrorMessage()), HttpStatus.NOT_FOUND
        );

    }


    @ExceptionHandler(AnswerNotFoundException.class)
    public ResponseEntity<ErrorResponse> answerNotFoundException(AnswerNotFoundException exe, WebRequest request){

        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(exe.getCode()).message(exe.getErrorMessage()), HttpStatus.NOT_FOUND
        );

    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> genericExceptionHandler(Exception exe, WebRequest request){

        return new ResponseEntity<ErrorResponse>(
        		new ErrorResponse().code("GEN-001").message("An unexpected error occurred. Please contact System Administrator"), HttpStatus.INTERNAL_SERVER_ERROR
        );

    }




}


