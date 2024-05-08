package org.example.elasticsearch.Service;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import org.example.clients.Entities.Article;
import org.example.clients.Entities.PreProcessedArticle;
import org.example.elasticsearch.Entities.ElasticArticle;
import org.example.elasticsearch.Repository.ElasticArticleRepository;
import org.example.elasticsearch.dto.BooleanSearchRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
                .time(article.getTime())
                .source(article.getSource())
                .category(article.getCategory())
                .views(article.getViews())
                .build();
    }

    public void saveArticles(List<Article> preProcessedArticles) {
        List<ElasticArticle> elasticArticles = new ArrayList<>();
        try {
            for (Article article : preProcessedArticles) {
                elasticArticles.add(transformArticleToElasticArticle(article));
            }
            articleRepository.saveAll(elasticArticles);
            log.info("All Articles were saved to Elasticsearch");
        } catch (Exception e) {
            log.error("An error occurred while saving articles to Elasticsearch", e);
        }
    }

    public SearchResponse<ElasticArticle> dynamicBoolQueryImpl(BooleanSearchRequest searchParams) throws IOException {
        BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();

        // Add must (AND) conditions
        for (Map<String, String> mustParams : searchParams.getMust()) {
            mustParams.forEach((key, value) -> {
                boolQueryBuilder.must(Query.of(q -> q.term(t -> t.field(key).value(value))));
            });
        }

        // Add should (OR) conditions
        for (Map<String, String> shouldParams : searchParams.getShould()) {
            shouldParams.forEach((key, value) -> {
                boolQueryBuilder.should(Query.of(q -> q.term(t -> t.field(key).value(value))));
            });
        }

        // Add must_not (NOT) conditions
        for (Map<String, String> mustNotParams : searchParams.getMust_not()) {
            mustNotParams.forEach((key, value) -> {
                boolQueryBuilder.mustNot(Query.of(q -> q.term(t -> t.field(key).value(value))));
            });
        }

        Query query = Query.of(q -> q.bool(boolQueryBuilder.build()));
        return elasticsearchClient.search(s -> s.index("articles").query(query).size(1000), ElasticArticle.class);
    }
}
