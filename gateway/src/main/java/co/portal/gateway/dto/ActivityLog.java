package co.portal.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ActivityLog {

    private String username;

    private String statusCode;

    private Object requestBody;

    private Object responseBody;

    private String requestURI;

    private LocalDate timestamp;

}
