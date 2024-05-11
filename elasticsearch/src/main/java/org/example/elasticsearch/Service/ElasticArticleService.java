package org.example.elasticsearch.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.example.clients.Entities.Article;
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
    private final RestHighLevelClient client = new RestHighLevelClient(
            RestClient.builder(new HttpHost("localhost", 9200, "http")));

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

    public SearchResponse dynamicBoolQueryImpl(BooleanSearchRequest searchParams) throws IOException {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // Add must (AND) conditions
        for (Map<String, String> mustParams : searchParams.getMust()) {
            mustParams.forEach((key, value) -> {
                boolQueryBuilder.must(QueryBuilders.termQuery(key, value));
            });
        }

        // Add should (OR) conditions
        for (Map<String, String> shouldParams : searchParams.getShould()) {
            shouldParams.forEach((key, value) -> {
                boolQueryBuilder.should(QueryBuilders.termQuery(key, value));
            });
        }

        // Add must_not (NOT) conditions
        for (Map<String, String> mustNotParams : searchParams.getMust_not()) {
            mustNotParams.forEach((key, value) -> {
                boolQueryBuilder.mustNot(QueryBuilders.termQuery(key, value));
            });
        }

//        Query query = Query.of(q -> q.bool(boolQueryBuilder.build()));

        TermsAggregationBuilder categoryAggregation = AggregationBuilders.terms("category_agg").field("category.keyword");
        TermsAggregationBuilder sourceAggregation = AggregationBuilders.terms("source_agg").field("source.keyword");

        SearchRequest searchRequest = new SearchRequest("articles");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.aggregation(categoryAggregation);
        searchSourceBuilder.aggregation(sourceAggregation);
        searchSourceBuilder.size(1000);
        searchRequest.source(searchSourceBuilder);

        org.elasticsearch.action.search.SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        return searchResponse;

//        return elasticsearchClient.search(s -> s.index("articles").query(query).size(1000), ElasticArticle.class);
    }
}
