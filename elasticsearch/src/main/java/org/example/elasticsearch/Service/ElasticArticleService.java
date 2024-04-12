package org.example.elasticsearch.Service;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import org.example.clients.Entities.Article;
import org.example.elasticsearch.Entities.ElasticArticle;
import org.example.elasticsearch.Repository.ElasticArticleRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Service
@Slf4j
@AllArgsConstructor
public class ElasticArticleService {
    private final ElasticArticleRepository articleRepository;
    private final ElasticsearchClient elasticsearchClient;

    private ElasticArticle transformArticleToElasticArticle(Article article) {
        return ElasticArticle.builder()
                .id(article.getId())
                .uuid(article.getUuid())
                .url(article.getUrl())
                .title(article.getTitle())
                .content(article.getContent())
                .image(article.getImage())
                .source(article.getSource())
                .category(article.getCategory())
                .views(article.getViews())
                .build();
    }

    public void saveArticles(List<Article> articles) {
        List<ElasticArticle> elasticArticles = new ArrayList<>();
        try {
            for (Article article : articles) {
                elasticArticles.add(transformArticleToElasticArticle(article));
            }
            articleRepository.saveAll(elasticArticles);
            log.info("All Articles were saved to Elasticsearch");
        } catch (Exception e) {
            log.error("An error occurred while saving articles to Elasticsearch", e);
        }
    }

    public SearchResponse<ElasticArticle> dynamicBoolQueryImpl(Map<String, String> mustParams, List<Map<String, String>> shouldParamsList) throws IOException {
        BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();

        // Add must (AND) conditions
        mustParams.forEach((key, value) -> {
            boolQueryBuilder.must(Query.of(q -> q.term(t -> t.field(key).value(value))));
        });

        // Add should (OR) conditions
        for (Map<String, String> shouldParams : shouldParamsList) {
            BoolQuery.Builder innerBoolQueryBuilder = new BoolQuery.Builder();
            shouldParams.forEach((key, value) -> {
                innerBoolQueryBuilder.should(Query.of(q -> q.term(t -> t.field(key).value(value))));
            });
            boolQueryBuilder.should(Query.of(q -> q.bool(innerBoolQueryBuilder.build())));
        }

        Query query = Query.of(q -> q.bool(boolQueryBuilder.minimumShouldMatch("1").build()));
        return elasticsearchClient.search(s -> s.index("articles").query(query), ElasticArticle.class);
    }
}
