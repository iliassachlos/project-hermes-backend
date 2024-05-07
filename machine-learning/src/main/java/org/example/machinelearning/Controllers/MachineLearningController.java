package org.example.machinelearning.Controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.machinelearning.Services.MachineLearningService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@CrossOrigin(origins = "*")
@RequestMapping("api/machine-learning")
@RequiredArgsConstructor
public class MachineLearningController {
    private final MachineLearningService machineLearningService;

    @GetMapping("/status")
    public ResponseEntity<Boolean> checkMachineLearningServiceStatus() {
        log.info("Fetched machine-learning status");
        return ResponseEntity.status(HttpStatus.OK).body(true);
    }

    @PostMapping("/sentiment")
    public Double sentimentAnalysis(@RequestBody String content) {
        return machineLearningService.sentimentAnalysis(content);
    }
}
