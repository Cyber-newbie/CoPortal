package paybank.astro.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import paybank.astro.dto.user.ErrorResponse;

import javax.validation.UnexpectedTypeException;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        System.out.println("Validation handler invoked >>> ");
            List<String> error = new ArrayList<>();
        for(FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            error.add(fieldError.getField() + ": " + fieldError.getDefaultMessage());
        }

        // Create and return the error response
        ErrorResponse errorResponse = new ErrorResponse("400", "Bad request");
        errorResponse.setErrors(error);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse("404", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        ErrorResponse errorResponse = new ErrorResponse("400", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(UnexpectedTypeException.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedType(UnexpectedTypeException ex) {
        System.out.println("Validation handler invoked >>> ");
        List<String> error = new ArrayList<>();
        error.add(ex.getMessage());

        // Create and return the error response
        ErrorResponse errorResponse = new ErrorResponse("400", "Bad request");
        errorResponse.setErrors(error);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}