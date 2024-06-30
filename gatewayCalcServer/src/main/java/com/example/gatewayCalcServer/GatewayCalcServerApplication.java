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
				.route("functionRepositoryServer",r->r.path("/СRepo/**")
						.uri("http://localhost:8090/"))
				.route("calculatorFuncServer",r->r.path("/list/**")
						.uri("http://localhost:8080/"))
				.route("calculatorRangeTableServer",r->r.path("/RTable/**")
						.uri("http://localhost:8080/"))
				.route("calculatorFuncTableServer",r->r.path("/FTable/**")
						.uri("http://localhost:8080/"))

				.route("functionRepositoryServerHTML",r->r.path("/customFuncRepo/**")
				.uri("http://localhost:8090/"))
				.route("calculatorFuncServerHTML",r->r.path("/funcList/**")
						.uri("http://localhost:8080/"))
				.route("calculatorRangeTableServerHTML",r->r.path("/rangeTable/**")
						.uri("http://localhost:8080/"))
				.route("calculatorFuncTableServerHTML",r->r.path("/funcTable/**")
						.uri("http://localhost:8080/"))
				.build();
	}

}
