package org.example.scraping.rabbitmq;

import feign.FeignException;
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
    public void saveArticle(List<Article> articles) {
        try {
            log.info("Sending articles to Elasticsearch: {}", articles);
            elasticsearchClient.saveArticles(articles);
            log.info("Successfully saved articles to Elasticsearch");
        } catch (FeignException.BadRequest e) {
            log.error("Bad Request: {}", e.request().body());
            throw e;
        } catch (Exception e) {
            log.error("Error occurred while saving articles to Elasticsearch", e);
            throw e;
        }
    }
}
