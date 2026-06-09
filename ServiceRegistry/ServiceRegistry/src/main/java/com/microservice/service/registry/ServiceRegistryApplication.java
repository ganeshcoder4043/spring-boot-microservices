package com.microservice.service.registry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer   // Spring Boot application ko Eureka Server banata hai.
public class ServiceRegistryApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceRegistryApplication.class, args);
		System.out.println("  _   _   _   _   _   _   _   _   _   _   _   _");
		System.out.println(" / \\ / \\ / \\ / \\ / \\ / \\ / \\ / \\ / \\ / \\ / \\ / \\");
		System.out.println("( S ( E ( R ( V ( I ( C ( E (   ( R ( E ( G ( I ( S ( T ( R ( Y )");
		System.out.println(" \\_/ \\_/ \\_/ \\_/ \\_/ \\_/ \\_/ \\_/ \\_/ \\_/ \\_/ \\_/");
		System.out.println("         SERVICE REGISTRY IS RUNNING...");
	}

}
