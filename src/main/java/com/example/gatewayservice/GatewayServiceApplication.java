package com.example.gatewayservice;

import com.example.filter.AuthenticationFilter;
import com.example.filter.AuthenticationFilter.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.cloud.gateway.discovery.DiscoveryClientRouteDefinitionLocator;
import org.springframework.cloud.gateway.discovery.DiscoveryLocatorProperties;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = { "com.example.*" })
public class GatewayServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayServiceApplication.class, args);
	}

	@Autowired
	AuthenticationFilter authenticationFilter;

	//@Bean
	RouteLocator staticRoute(RouteLocatorBuilder builder){
		return builder.routes().route(r->r.path("/customers/**").uri("http://localhost:8081/"))
				.route(r->r.path("/products/**").uri("http://localhost:8082/"))
				.build();
	}


	//@Bean
	RouteLocator hybridRoute(RouteLocatorBuilder builder){
		GatewayFilter gatewayFilter = authenticationFilter.apply(new Config());
		return builder.routes().route(r-> r.path("/customers/**").filters(spec -> spec.filter(gatewayFilter))

							.uri("lb://CUSTOMER-SERVICE"))

				.route(r->r.path("/products/**").uri("lb://PRODUCT-SERVICE"))
				.build();
	}



	//@Bean
	DiscoveryClientRouteDefinitionLocator dynamicRoute(ReactiveDiscoveryClient rdc, DiscoveryLocatorProperties dlp){
		return new DiscoveryClientRouteDefinitionLocator(rdc,dlp);
	}

}
