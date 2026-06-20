package com.peerpay.gateway.filter;

import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Global pre/post filter that:
 * 1. Generates a unique X-Request-ID (trace ID) for every incoming request
 * 2. Logs request details (method, path, client IP)
 * 3. Injects the trace ID into downstream request headers for log correlation
 * 4. Logs response details (status code, latency in ms)
 */
@Component
public class RequestLoggingFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);
    private static final String REQUEST_ID_HEADER = "X-Request-ID";
    private static final String REQUEST_START_TIME = "requestStartTime";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // Generate or extract trace ID
        String requestId = request.getHeaders().getFirst(REQUEST_ID_HEADER);
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
        }

        // Store start time for latency calculation
        exchange.getAttributes().put(REQUEST_START_TIME, System.currentTimeMillis());

        // Inject trace ID into MDC for structured logging
        String finalRequestId = requestId;
        MDC.put("traceId", finalRequestId);

        // Log incoming request
        log.info("➜ {} {} | Client: {} | TraceID: {}",
                request.getMethod(),
                request.getURI().getPath(),
                request.getRemoteAddress() != null ? request.getRemoteAddress().getAddress().getHostAddress() : "unknown",
                finalRequestId);

        // Add trace ID to downstream request headers
        ServerHttpRequest mutatedRequest = request.mutate()
                .header(REQUEST_ID_HEADER, finalRequestId)
                .build();

        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(mutatedRequest)
                .build();

        return chain.filter(mutatedExchange).then(Mono.fromRunnable(() -> {
            ServerHttpResponse response = mutatedExchange.getResponse();
            Long startTime = mutatedExchange.getAttribute(REQUEST_START_TIME);
            long latency = startTime != null ? System.currentTimeMillis() - startTime : -1;

            // Add trace ID to response headers
            response.getHeaders().add(REQUEST_ID_HEADER, finalRequestId);

            log.info("✓ {} {} | Status: {} | Latency: {}ms | TraceID: {}",
                    request.getMethod(),
                    request.getURI().getPath(),
                    response.getStatusCode() != null ? response.getStatusCode().value() : "N/A",
                    latency,
                    finalRequestId);

            MDC.clear();
        }));
    }

    @Override
    public int getOrder() {
        // Run first – before rate limiting and auth
        return -3;
    }
}
