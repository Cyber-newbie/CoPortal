package co.portal.logging_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Builder
@AllArgsConstructor
@Data
@NoArgsConstructor
public class LogRequest implements Serializable {

    @JsonProperty
    private String username;

    @JsonProperty
    private String statusCode;

    @JsonProperty
    private String ipAddress;

    @JsonProperty
    private String requestBody;

    @JsonProperty
    private String responseBody;

    @JsonProperty
    private String requestURI;

    @JsonProperty
    private LocalDate timestamp;

}
