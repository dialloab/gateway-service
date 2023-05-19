package com.example.filter;

import com.example.roles.Role;
import com.example.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {


    @Autowired
    private JwtUtil jwtUtil;

    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {

                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    throw new RuntimeException("missing authorization header");
                }

                String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    authHeader = authHeader.substring(7);
                }
                try {
                    jwtUtil.validateToken(authHeader);

                } catch (Exception e) {
                    System.out.println("invalid access !");
                    throw new RuntimeException("unauthorized access to application");
                }
                if(exchange.getRequest().getHeaders().get("ROLE") == null){
                    System.out.println("Role empty !");
                    throw new RuntimeException("Empty role");
                }
                String roleHeader = exchange.getRequest().getHeaders().get("Role").get(0).toString();
                System.out.println("Role POSTMAN : "+roleHeader);

            boolean oneMatch = Arrays.stream(Role.values()).anyMatch(a -> roleHeader.equals(a.name()));
            if (!oneMatch){
                System.out.println("invalid role !");
                throw new RuntimeException("unauthorized role");
            }
            return chain.filter(exchange);
        });
    }

    public static class Config {

    }
}