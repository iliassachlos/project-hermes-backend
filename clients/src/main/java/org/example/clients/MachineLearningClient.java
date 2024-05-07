package org.example.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("machine-learning")
public interface MachineLearningClient {

    @PostMapping("/api/machine-learning/sentiment")
    Double sentimentAnalysis(@RequestBody String content);
}
