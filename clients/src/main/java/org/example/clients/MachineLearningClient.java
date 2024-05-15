package org.example.clients;

import org.example.clients.dto.scraping.SentimentRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "machine-learning",url = "http://localhost:8000")
public interface MachineLearningClient {

    @PostMapping("/api/machine-learning/sentiment")
    Double sentimentAnalysis(@RequestBody SentimentRequest articleContent);
}
