package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {

    @Autowired
    private GatewayFilter jwtGatewayFilter;

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder routeLocatorBuilder) {

        return routeLocatorBuilder.routes()
                .route("jwt-demo", rt -> rt.path("/authenticate")
                        .uri("http://localhost:8090/"))
                .route("customer-mongo", rt -> rt.path("/customer/**")
                        .filters((f -> f.filter(jwtGatewayFilter)))
                        .uri("http://localhost:8060/"))
                .route("account-sql", rt -> rt.path("/account/**")
                        .filters((f -> f.filter(jwtGatewayFilter)))
                        .uri("http://localhost:8065/"))
                .build();
    }
}
