package co.portal.submission_service.exception;

public class TotalAttempExceedException extends RuntimeException {
    public TotalAttempExceedException(String message) {
        super(message);
    }
}
