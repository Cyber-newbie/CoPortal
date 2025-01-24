package co.portal.submission_service.exception;

import co.portal.submission_service.dto.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAllExceptions(Exception ex){

        log.error("An error occurred {}", String.valueOf(ex));
        String message = Optional.ofNullable(ex.getMessage()).orElse("Internal Server error");
        HashMap<String, Object> errorDetails = new HashMap<>();

        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorDetails.put("message", message);

        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @ExceptionHandler(TimeLimitExceedException.class)
    public Response<Object> handleTimiLimitException(TimeLimitExceedException e){
            log.error("Time limit error occurred {}", e.getMessage());

            Response<Object> response = new Response<>(null);
            response.setStatus(HttpStatus.BAD_REQUEST.name());
            response.setMessage(e.getMessage());
            return  response;

    }

    @ExceptionHandler(TotalAttempExceedException.class)
    public Response<Object> TotalAttemptsException(TimeLimitExceedException e){
        log.error("Total attempts error occurred {}", e.getMessage());

        Response<Object> response = new Response<>(null);
        response.setStatus(HttpStatus.BAD_REQUEST.name());
        response.setMessage(e.getMessage());
        return  response;

    }

}
