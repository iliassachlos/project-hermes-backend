package org.example.scraping;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "org.example.clients")
public class ScrapingApplication {
    public static void main(String[] args) {
        SpringApplication.run(ScrapingApplication.class, args);
    }
}
