package co.portal.gateway.config;

import co.portal.gateway.dto.ActivityLog;
import co.portal.gateway.dto.RequestInfo;
import co.portal.gateway.service.RabbitMQService;
import co.portal.gateway.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicReference;

@Component
@Slf4j
public class RequestInterceptFilter implements GlobalFilter, Ordered {

    @Autowired
    private RabbitMQService rabbitMQService;

    @Autowired
    private Utils utils;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {


        log.info("GL0BAL FILTER INVOKED");

        ServerHttpRequest originalRequest = exchange.getRequest();
        ServerHttpResponse originalResponse = exchange.getResponse();
        AtomicReference<ServerHttpRequest> decoratedRequest = new AtomicReference<>(originalRequest);
        ActivityLog activityLog = new ActivityLog();
        RequestInfo requestInfo = utils.getRequestInfo(originalRequest);
        HttpStatus status = originalResponse.getStatusCode();

        DataBufferFactory bufferFactory = originalResponse.bufferFactory();
//        requestInfo.getMethod().matches("GET")
        //not capturing request if it's a GET
        if(requestInfo.getMethod().matches("GET")){

            ServerHttpResponseDecorator decoratedResponse = responseDecorator(originalResponse, originalRequest, null);

            // Proceed with the chain using the decorated request and response
            return chain.filter(exchange.mutate()
                    .request(decoratedRequest.get())
                    .response(decoratedResponse)
                    .build());
        }  else {

        // Capture and log the request body
        return DataBufferUtils.join(originalRequest.getBody())
                .flatMap(requestDataBuffer -> {
                    String requestBody = StandardCharsets.UTF_8.decode(requestDataBuffer.asByteBuffer()).toString().trim();
                    log.info("LAST BLOCK Incoming Request: [{}] {} status: {} with Body: {}", requestInfo.getMethod(), requestInfo.getRequestURI(), status, requestBody);

                    // Create and send ActivityLog for request

                    activityLog.setRequestBody(requestBody);


//                    rabbitMQService.publishLog(activityLog, "request");

                    // Recreate the DataBuffer for the request body
                    decoratedRequest.set(new ServerHttpRequestDecorator(originalRequest) {
                        @Override
                        public Flux<DataBuffer> getBody() {
                            return Flux.just(bufferFactory.wrap(requestBody.getBytes(StandardCharsets.UTF_8)));
                        }
                    });


                    // Capture and log the response body
                    ServerHttpResponseDecorator decoratedResponse = responseDecorator(originalResponse, originalRequest, requestBody);

                    // Proceed with the chain using the decorated request and response
                    return chain.filter(exchange.mutate()
                            .request(decoratedRequest.get())
                            .response(decoratedResponse)
                            .build());
                });

        }

    }


    //decorates response body to capture it and also receives string request body and log to publish via rabbitmq
    private ServerHttpResponseDecorator responseDecorator(ServerHttpResponse originalResponse,
                                                          ServerHttpRequest originalRequest,
                                                          String requestBody) {
        return new ServerHttpResponseDecorator(originalResponse) {
            final DataBufferFactory bufferFactory = originalResponse.bufferFactory();

            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                // Check the status code to handle cases with or without response body
                HttpStatus status = originalResponse.getStatusCode();


                if (utils.hasNoResponseBody(status)) {
                    log.info("Outgoing Response: Status Code [{}], No Response Body", status);

                    // Create and send ActivityLog for responses without body
                    ActivityLog activityLog = utils.prepareActivityLog(originalRequest, originalResponse, null, requestBody);
                    rabbitMQService.publishLog(activityLog, "CREATE");

                    // Proceed without modifying the body
                    return super.writeWith(body);
                }

                // Handle responses with a body
                return super.writeWith(Flux.from(body).flatMap(responseDataBuffer -> {
                    String responseBody = StandardCharsets.UTF_8
                            .decode(responseDataBuffer.asByteBuffer())
                            .toString().trim();

                    log.info("Outgoing Response: Status Code [{}], Body: {}", status, responseBody);

                    // Create and send ActivityLog for response
                    ActivityLog activityLog = utils.prepareActivityLog(originalRequest, originalResponse, responseBody, requestBody);
                    rabbitMQService.publishLog(activityLog, "CREATE");

                    // Recreate the DataBuffer for the response body
                    DataBuffer newResponseBuffer = bufferFactory.wrap(responseBody.getBytes(StandardCharsets.UTF_8));
                    return Flux.just(newResponseBuffer);
                }));
            }
        };
    }


    private void logResponse(ServerWebExchange exchange, ActivityLog activityLog){

        log.info("GATEWAY RESPONSE FILTER");

        ServerHttpResponse originalResponse = exchange.getResponse();
        DataBufferFactory bufferFactory = originalResponse.bufferFactory();

        // Create a decorated response to intercept and log the body
        ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                // Convert Publisher to Flux to use reactive operators
                return super.writeWith(Flux.from(body).flatMap(dataBuffer -> {
                    // Convert the DataBuffer to a String
                    String responseBody = StandardCharsets.UTF_8.decode(dataBuffer.asByteBuffer()).toString();
                    String statusCode = originalResponse.getStatusCode() != null ?
                            originalResponse.getStatusCode().toString() : null;

                    log.info("Response Body: {}", responseBody);
                    activityLog.setResponseBody(responseBody);
                    activityLog.setStatusCode(statusCode);
                    activityLog.setUsername(utils.getUsernameFromResponse(responseBody));


                    rabbitMQService.publishLog(activityLog, "CREATE");

                    // Re-create a DataBuffer with the same content to forward to the client
                    DataBuffer newBuffer = bufferFactory.wrap(responseBody.getBytes(StandardCharsets.UTF_8));
                    return Flux.just(newBuffer);
                }));
            }
        };

        // Proceed with the chain using the decorated response
         exchange.mutate().response(decoratedResponse).build();
    }



    @Override
    public int getOrder() {
        return -1;
    }
}