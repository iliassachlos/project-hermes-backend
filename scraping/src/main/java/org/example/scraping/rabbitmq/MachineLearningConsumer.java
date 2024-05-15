package org.example.scraping.rabbitmq;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.clients.MachineLearningClient;
import org.example.clients.dto.scraping.SentimentRequest;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class MachineLearningConsumer {

    private final MachineLearningClient machineLearningClient;

    @RabbitListener(queues = "${rabbitmq.queues.machine-learning}")
    public Double performMachineLearning(SentimentRequest content) {
        return machineLearningClient.sentimentAnalysis(content);
    }
}