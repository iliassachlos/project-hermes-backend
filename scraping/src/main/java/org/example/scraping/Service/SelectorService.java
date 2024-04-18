package org.example.scraping.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.scraping.Entities.Selector;
import org.example.scraping.Repositories.SelectorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class SelectorService {
    private final SelectorRepository selectorRepository;

    public ResponseEntity<Selector> addSelector(String id, String newSelector) {
        try {
            Selector selector = selectorRepository.findById(id).orElse(null);
            if (selector == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            } else {
                selector.getSelectors().add(newSelector);
                selectorRepository.save(selector);
                return ResponseEntity.status(HttpStatus.OK).body(selector);
            }
        } catch (Exception e) {
            log.error("An error occurred while adding a new selector", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    public ResponseEntity<Selector> removeSelector(String id, String selectorToRemove) {
        try {
            Selector selector = selectorRepository.findById(id).orElse(null);
            if (selector == null) {
                log.error("Selector with id {} not found", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            } else {
                selector.getSelectors().remove(selectorToRemove);
                selectorRepository.save(selector);
                return ResponseEntity.status(HttpStatus.OK).body(selector);
            }
        } catch (Exception e) {
            log.error("An error occurred while removing a selector", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
