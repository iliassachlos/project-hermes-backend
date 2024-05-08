package org.example.scraping.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.scraping.Entities.Selector;
import org.example.scraping.Repositories.SelectorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class SelectorService {

    private final SelectorRepository selectorRepository;

    public ResponseEntity<List<Selector>> getAllSelectors() {
        List<Selector> selectors = selectorRepository.findAll();
        if (selectors.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(selectors);
    }

    public ResponseEntity<Selector> addSelector(String uuid, String newSelector) {
        try {
            Selector selector = selectorRepository.findById(uuid).orElse(null);
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

    public ResponseEntity<Selector> removeSelector(String uuid, String selectorToRemove) {
        try {
            Selector selector = selectorRepository.findById(uuid).orElse(null);
            if (selector == null) {
                log.error("Selector with id {} not found", uuid);
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
