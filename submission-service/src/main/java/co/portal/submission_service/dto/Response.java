package co.portal.submission_service.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Response<T> {

    private String status;

    private String message;

    private T data;

    public Response(T data) {
        this.data = data;
    }
}
