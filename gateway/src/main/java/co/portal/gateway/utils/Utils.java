package co.portal.gateway.utils;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Service
public class Utils {

    public enum LogsActions {

        CREATE,
        UPDATE,

    }

    public static Mono<String> captureResponseBody(org.reactivestreams.Publisher<? extends DataBuffer> body) {
        return DataBufferUtils.join(body)
                .map(dataBuffer -> {
                    try {
                        // Convert DataBuffer to String
                        byte[] content = new byte[dataBuffer.readableByteCount()];
                        dataBuffer.read(content);
                        return new String(content, StandardCharsets.UTF_8);
                    } finally {
                        DataBufferUtils.release(dataBuffer); // Release buffer
                    }
                });
    }

}
