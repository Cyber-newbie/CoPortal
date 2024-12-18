package co.portal.logging_service.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

public class LogRequest implements Serializable {

    private String username;

    private String statusCode;

    private Object requestBody;

    private Object responseBody;

    private String requestURI;

    private LocalDate timestamp;

    private String ipAddress;

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public LocalDate getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDate timestamp) {
        this.timestamp = timestamp;
    }

    public String getRequestURI() {
        return requestURI;
    }

    public void setRequestURI(String requestURI) {
        this.requestURI = requestURI;
    }

    public Object getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(Object responseBody) {
        this.responseBody = responseBody;
    }

    public Object getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(Object requestBody) {
        this.requestBody = requestBody;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return "LogRequest{" +
                "username='" + username + '\'' +
                ", statusCode='" + statusCode + '\'' +
                ", requestBody=" + requestBody +
                ", responseBody=" + responseBody +
                ", requestURI='" + requestURI + '\'' +
                ", timestamp=" + timestamp +
                ", ipAddress='" + ipAddress + '\'' +
                '}';
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
