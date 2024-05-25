package org.example.scraping.rabbitmq;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.clients.ElasticsearchClient;
import org.example.clients.Entities.Article;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@AllArgsConstructor
public class ElasticSaverConsumer {

    private final ElasticsearchClient elasticsearchClient;

    @RabbitListener(queues = "${rabbitmq.queues.elastic-saver}")
    public void saveArticle(List<Article> preProcessedArticles) {
        elasticsearchClient.saveArticles(preProcessedArticles);
        log.info("Finished saving to elastic");
    }
}
