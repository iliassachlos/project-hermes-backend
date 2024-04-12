package org.example.elasticsearch.Repository;

import org.example.elasticsearch.Entities.ElasticArticle;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElasticArticleRepository extends ElasticsearchRepository<ElasticArticle, String> {
}
