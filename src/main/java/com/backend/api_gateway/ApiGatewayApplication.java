package com.backend.api_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collections;

@SpringBootApplication
@RestController
public class ApiGatewayApplication {

	@Bean
	public CorsWebFilter corsWebFilter() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Collections.singletonList("http://localhost:5173")); // Allow specific origin
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE")); // Include OPTIONS
		configuration.setAllowedHeaders(Arrays.asList("Content-Type", "Authorization")); // Allow headers
		configuration.setAllowCredentials(true); // Allow credentials (if needed)

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);

		return new CorsWebFilter(source);
	}

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}

	@Bean
	public RouteLocator myRoutes(RouteLocatorBuilder builder, AuthenticationFilter authenticationFilter) {
		return builder.routes()
				.route(p -> p.
						path("/auth/**").uri("http://localhost:5000"))
				.route(p -> p
						.path("/users/**")
						.filters(f -> f.filter(authenticationFilter))
						.uri("http://localhost:5000"))
				.route(p -> p
						.path("/profile/**")
						.filters(f -> f.filter(authenticationFilter))
						.uri("http://localhost:5001"))
				.route(p -> p
						.path("/events/**")
						.filters(f -> f.filter(authenticationFilter))
						.uri("http://localhost:5002"))
				.build();
	}

	@RequestMapping("/fallback")
	public Mono<String> fallback() {
		return Mono.just("fallback");
	}

}
