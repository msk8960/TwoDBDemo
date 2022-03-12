package com.example.demo.filter;

import com.example.demo.util.JWTUtility;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JWTGatewayFilter implements GatewayFilter {

    @Autowired
    private JWTUtility jwtUtility;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = (ServerHttpRequest) exchange.getRequest();

        if (!request.getHeaders().containsKey("Authorization")) {
            return completeResponse(exchange, HttpStatus.UNAUTHORIZED);
        }

        final String token = request.getHeaders()
                .getOrEmpty("Authorization").get(0).substring(7);

        try {
            if (!jwtUtility.validateToken(token)) {
                return completeResponse(exchange, HttpStatus.UNAUTHORIZED);
            }
        } catch (MalformedJwtException | ExpiredJwtException e) {
            return completeResponse(exchange, HttpStatus.BAD_REQUEST);
        }

        exchange.getRequest().mutate()
                .header("user", jwtUtility.getUsernameFromToken(token))
                .build();

        return chain.filter(exchange);
    }

    private Mono<Void> completeResponse(ServerWebExchange exchange, HttpStatus errorStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(errorStatus);
        return response.setComplete();
    }
}

