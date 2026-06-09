package com.microservice.hotel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class HotelServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(HotelServiceApplication.class, args);
		System.out.println("  _   _   _   _   _   _   _   _   _   _   _");
		System.out.println(" / \\ / \\ / \\ / \\ / \\ / \\ / \\ / \\ / \\ / \\ / \\");
		System.out.println("( H ( O ( T ( E ( L (  ( S ( E ( R ( V ( I ( C )");
		System.out.println(" \\_/ \\_/ \\_/ \\_/ \\_/ \\_/ \\_/ \\_/ \\_/ \\_/ \\_/");
		System.out.println("         APPLICATION IS RUNNING...");
	}

}
