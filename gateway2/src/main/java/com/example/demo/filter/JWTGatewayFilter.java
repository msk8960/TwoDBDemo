package com.example.demo.filter;

import com.example.demo.model.DAOUser;
import com.example.demo.repo.UserRepo;
import com.example.demo.util.JWTUtility;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Component
public class JWTGatewayFilter implements GatewayFilter {

    @Autowired
    private JWTUtility jwtUtility;

    @Autowired
    private UserRepo userRepo;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();

        List<String> authHeaders = request.getHeaders().getOrEmpty(HttpHeaders.AUTHORIZATION);
        if (authHeaders.isEmpty()) {
            return completeResponse(exchange, HttpStatus.UNAUTHORIZED);
        }

        final String authHeader = authHeaders.iterator().next();

        if (!authHeader.startsWith("Bearer ")) {
            return completeResponse(exchange, HttpStatus.BAD_REQUEST);
        }

        final String token = authHeader.substring(7);

        try {
            final String username = jwtUtility.getUsernameFromToken(token);
            Optional<DAOUser> user = userRepo.findByUsername(username);

            if (!user.isPresent()) {
                return completeResponse(exchange, HttpStatus.UNAUTHORIZED);
            }

            if (!jwtUtility.validateToken(token, user.get())) {
                return completeResponse(exchange, HttpStatus.UNAUTHORIZED);
            }

            request.mutate()
                    .header("user", username)
                    .build();

            return chain.filter(exchange);
        } catch (MalformedJwtException | ExpiredJwtException e) {
            return completeResponse(exchange, HttpStatus.BAD_REQUEST);
        }
    }

    private Mono<Void> completeResponse(ServerWebExchange exchange, HttpStatus errorStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(errorStatus);
        return response.setComplete();
    }
}

