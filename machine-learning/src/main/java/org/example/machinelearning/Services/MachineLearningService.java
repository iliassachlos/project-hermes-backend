package org.example.machinelearning.Services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.machinelearning.Util.MachineLearningUtil;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class MachineLearningService {
    public Double sentimentAnalysis(String content) {
        try {
            return MachineLearningUtil.sentimentAnalysis(content);
        } catch (Exception e) {
            log.error("An error occurred while processing the machine learning: {}", e.getMessage());
            return null;
        }
    }
}
