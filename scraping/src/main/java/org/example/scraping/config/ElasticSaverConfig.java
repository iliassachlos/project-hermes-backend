package org.example.scraping.config;

import lombok.Getter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;

@Getter
@Configuration
public class ElasticSaverConfig {
    @Value("${rabbitmq.exchanges.internal}")
    private String internalExchange;

    @Value("${rabbitmq.queues.elastic-saver}")
    private String elasticSaverQueue;

    @Value("${rabbitmq.routing-keys.internal-elastic-saver}")
    private String internalElasticSaverRoutingKey;

    @Bean
    public TopicExchange internalTopicExchange() {
        return new TopicExchange(this.internalExchange);
    }

    @Bean
    public Queue elasticSaverQueue() {
        return new Queue(this.elasticSaverQueue);
    }

    @Bean
    public Binding internalArticleBinding() {
        return BindingBuilder
                .bind(elasticSaverQueue())
                .to(internalTopicExchange())
                .with(this.internalElasticSaverRoutingKey);
    }
}
