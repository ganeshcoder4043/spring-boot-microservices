package com.microservice.user.service.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class MyConfig {

    @Bean
    @LoadBalanced  // RestTemplate ko batata hai ki "http://SERVICE_NAME" use Karo, aur LoadBalancer automatically us service ka actual IP aur port find karega."
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
