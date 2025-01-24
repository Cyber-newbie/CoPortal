package co.portal.submission_service.dto.submission;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class SubmissionResponse<T> {

    private String status;

    private String message;

    private T data;

    public SubmissionResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }
}
