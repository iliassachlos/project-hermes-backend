package org.example.article.rabbitmq;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.article.Services.ArticleService;
import org.example.clients.Entities.Article;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@AllArgsConstructor
public class ArticleConsumer {

    private ArticleService articleService;

    @RabbitListener(queues = "${rabbitmq.queues.article}")
    public void receiveArticle(List<Article> articles) {
        log.info("Received message from article-queue: {}", articles.get(0));
        articleService.getAllArticles();
    }
}
