package com.zerotrust.auth_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class AuthGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthGatewayApplication.class, args);
	}

}
