package co.portal.gateway.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.sql.DataSourceDefinitions;

@Data
@Builder
public class RequestInfo {

    private String clientIp;

    private String method;

    private String requestURI;


}
