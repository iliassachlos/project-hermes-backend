package org.example.article.config;

import lombok.Getter;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class ArticleConfig {

    @Value("${rabbitmq.exchanges.internal}")
    private String internalExchange;

    @Value("${rabbitmq.queues.article}")
    private String articleQueue;

    @Value("${rabbitmq.routing-keys.internal-article}")
    private String internalArticleRoutingKey;

    @Bean
    public TopicExchange internalTopicExchange() {
        return new TopicExchange(this.internalExchange);
    }

    @Bean
    public Queue articleQueue() {
        return new Queue(this.articleQueue);
    }

    @Bean
    public Binding internalArticleBinding() {
        return BindingBuilder
                .bind(articleQueue())
                .to(internalTopicExchange())
                .with(this.internalArticleRoutingKey);
    }
}
