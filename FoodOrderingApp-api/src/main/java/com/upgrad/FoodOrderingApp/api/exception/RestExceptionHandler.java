package com.upgrad.FoodOrderingApp.api.exception;

import com.upgrad.FoodOrderingApp.api.model.ErrorResponse;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> signUpRestrictedException(SignUpRestrictedException exc, WebRequest request){
        return new ResponseEntity<ErrorResponse>(new ErrorResponse()
                .code(exc.getCode())
                .message(exc.getErrorMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> authenticationFailedException(AuthenticationFailedException exc, WebRequest request) {
        return new ResponseEntity<ErrorResponse>( new ErrorResponse()
                    .code(exc.getCode())
                    .message(exc.getErrorMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> authorizationFailedException(AuthorizationFailedException exc, WebRequest request) {
        return new ResponseEntity<ErrorResponse>( new ErrorResponse()
                .code(exc.getCode())
                .message(exc.getErrorMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> updateCustomerException(UpdateCustomerException exc, WebRequest request) {
        return new ResponseEntity<ErrorResponse>( new ErrorResponse()
                .code(exc.getCode())
                .message(exc.getErrorMessage()), HttpStatus.BAD_REQUEST);
    }




}
