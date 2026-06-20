package com.peerpay.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Token-bucket rate limiter using Redis.
 * Limits requests per client IP to prevent abuse.
 *
 * Configuration:
 * - MAX_REQUESTS: maximum requests per window
 * - WINDOW_SECONDS: sliding window duration
 */
@Component
public class RateLimitFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(RateLimitFilter.class);
    private static final int MAX_REQUESTS = 100;
    private static final int WINDOW_SECONDS = 60;
    private static final String KEY_PREFIX = "rate_limit:";

    private final ReactiveStringRedisTemplate redisTemplate;

    public RateLimitFilter(ReactiveStringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String clientIp = extractClientIp(exchange);
        String key = KEY_PREFIX + clientIp;

        return redisTemplate.opsForValue().increment(key)
                .flatMap(count -> {
                    if (count == 1) {
                        // First request in this window – set expiry
                        return redisTemplate.expire(key, Duration.ofSeconds(WINDOW_SECONDS))
                                .then(chain.filter(exchange));
                    }
                    if (count > MAX_REQUESTS) {
                        log.warn("Rate limit exceeded for IP: {} | Count: {}/{}", clientIp, count, MAX_REQUESTS);
                        exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                        exchange.getResponse().getHeaders().add("X-RateLimit-Limit", String.valueOf(MAX_REQUESTS));
                        exchange.getResponse().getHeaders().add("X-RateLimit-Remaining", "0");
                        return exchange.getResponse().setComplete();
                    }

                    exchange.getResponse().getHeaders().add("X-RateLimit-Limit", String.valueOf(MAX_REQUESTS));
                    exchange.getResponse().getHeaders().add("X-RateLimit-Remaining", String.valueOf(MAX_REQUESTS - count));
                    return chain.filter(exchange);
                });
    }

    private String extractClientIp(ServerWebExchange exchange) {
        // Check X-Forwarded-For header first (for load balancers/proxies)
        String forwarded = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        var remoteAddress = exchange.getRequest().getRemoteAddress();
        return remoteAddress != null ? remoteAddress.getAddress().getHostAddress() : "unknown";
    }

    @Override
    public int getOrder() {
        // Run after logging, before auth
        return -2;
    }
}
