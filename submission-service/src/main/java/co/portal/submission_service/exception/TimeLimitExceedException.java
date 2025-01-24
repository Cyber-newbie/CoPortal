package co.portal.submission_service.exception;

public class TimeLimitExceedException extends RuntimeException {
  public TimeLimitExceedException(String message) {
    super(message);
  }
}
