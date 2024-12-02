package paybank.astro.dto.submission;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SubmissionResponse<T> {

    private String status;

    private String message;

    private T data;

    public SubmissionResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }
}
