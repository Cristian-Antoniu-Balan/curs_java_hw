package ro.frit.library.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import ro.frit.library.service.EmailAlreadyInUseException;

@RestControllerAdvice 
public class ApiExceptionHandler {

    @ExceptionHandler(EmailAlreadyInUseException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String emailAlreadyInUse(EmailAlreadyInUseException ex) {
        return ex.getMessage();
    }
}
