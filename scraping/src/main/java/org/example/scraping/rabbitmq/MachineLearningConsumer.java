package org.example.scraping.rabbitmq;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.clients.Entities.PreProcessedArticle;
import org.example.clients.MachineLearningClient;
import org.example.scraping.Service.ScrapingService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@AllArgsConstructor
public class MachineLearningConsumer {

    private final MachineLearningClient machineLearningClient;

    @RabbitListener(queues = "${rabbitmq.queues.machine-learning}")
    public Double performMachineLearning(String content) {
        return machineLearningClient.sentimentAnalysis(content);
    }
}