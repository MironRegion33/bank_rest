package com.example.bankcards.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(InvalidUserDataException.class)
    public ResponseEntity<String> handleInvalidUserDataException(InvalidUserDataException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<String> handleUsernameAlreadyExistsException(UsernameAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
    @ExceptionHandler(InvalidCardNumberException.class)
    public ResponseEntity<String> handleInvalidCardNumberException(InvalidCardNumberException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
    @ExceptionHandler(CardNotFoundException.class)
    public ResponseEntity<String> handleCardNotFoundException(CardNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
    @ExceptionHandler(ForbiddenOperationException.class)
    public ResponseEntity<String> handleForbiddenOperationException(ForbiddenOperationException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }
    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<String> handleInsufficientFundsException(InsufficientFundsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
    @ExceptionHandler(InvalidTransferException.class)
    public ResponseEntity<String> handleInvalidTransferException(InvalidTransferException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
