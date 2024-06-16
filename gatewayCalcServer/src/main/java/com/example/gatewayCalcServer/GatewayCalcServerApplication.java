package com.example.gatewayCalcServer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GatewayCalcServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayCalcServerApplication.class, args);
	}

	/*
	      - id: functionRepositoryService
        uri: http://localhost:8090/
        predicates:
          - Path=/СRepo/**
      - id: calculatorFuncService
        uri: http://localhost:8080/
        predicates:
          - Path=/test/**
      - id: calculatorRangeTableService
        uri: http://localhost:8080/
        predicates:
          - Path=/tTable/**
	 */
	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()
				.route("functionRepositoryService",r->r.path("/СRepo/**")
						.uri("http://localhost:8090/"))
				.route("calculatorFuncService",r->r.path("/test/**")
						.uri("http://localhost:8080/"))
				.route("calculatorRangeTableService",r->r.path("/tTable/**")
						.uri("http://localhost:8080/")).build();
	}

}
