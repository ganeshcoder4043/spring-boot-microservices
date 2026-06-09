package com.microservice.user.service;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);


		//System.out.println("YOUR USER SERVICE APPLICATION IS RUNNING...");
        System.out.println("  _   _   _   _   _   _   _   _   _   _   _");
        System.out.println(" / \\ / \\ / \\ / \\ / \\ / \\ / \\ / \\ / \\ / \\ / \\");
        System.out.println("( U ( S ( E ( R (   ( S ( E ( R ( V ( I ( C )");
        System.out.println(" \\_/ \\_/ \\_/ \\_/ \\_/ \\_/ \\_/ \\_/ \\_/ \\_/ \\_/");
        System.out.println("         APPLICATION IS RUNNING...");
    }

}
