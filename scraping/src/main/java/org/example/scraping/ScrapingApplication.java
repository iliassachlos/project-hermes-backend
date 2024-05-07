package org.example.scraping;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableDiscoveryClient
@EnableRabbit
@EnableFeignClients(basePackages = "org.example.clients")
@SpringBootApplication(scanBasePackages = {"org.example.scraping", "org.example.amqp",})
@EntityScan({"org.example.clients.article.Entities", "org.example.scraping.Entities"})
@EnableScheduling
public class ScrapingApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScrapingApplication.class, args);
    }

}
