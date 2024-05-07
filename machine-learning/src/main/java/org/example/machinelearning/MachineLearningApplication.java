package org.example.machinelearning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "org.example.clients")
public class MachineLearningApplication {
    public static void main(String[] args) {
        SpringApplication.run(MachineLearningApplication.class, args);
    }
}
