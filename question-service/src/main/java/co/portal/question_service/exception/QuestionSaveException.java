package co.portal.question_service.exception;

public class QuestionSaveException extends RuntimeException {
    public QuestionSaveException(String message) {
        super(message);
    }

    public QuestionSaveException(String message, Throwable cause) {
        super(message, cause);
    }
}

