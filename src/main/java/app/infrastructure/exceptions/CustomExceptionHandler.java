package app.infrastructure.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;

@ControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler({CustomException.class, NoMethodsAvailableException.class})
    public final ResponseEntity<ErrorDetails> handleCustomException(RuntimeException ex) {
        String details = null;
        if (ex instanceof CustomException) {
            details = ((CustomException) ex).getDetails();
        } else if (ex instanceof NoMethodsAvailableException) {
            details = ((NoMethodsAvailableException) ex).getDetails();
        }
        ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(), details);
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}