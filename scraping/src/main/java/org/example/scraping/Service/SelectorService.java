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

    public ResponseEntity<Selector> getSelectorByUuid(String uuid) {
        try {
            Selector selector = selectorRepository.findByUuid(uuid);
            if (selector == null) {
                log.warn("Selector with uuid {} not found", uuid);
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
            return ResponseEntity.status(HttpStatus.OK).body(selector);
        } catch (Exception e) {
            log.error("Error while getting selector by uuid", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<String> addSelector(String uuid, String selector) {
        try {
            Selector existingSelector = selectorRepository.findByUuid(uuid);
            if (existingSelector == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Selector with uuid " + uuid + " not found");
            }
            existingSelector.getSelectors().add(selector);
            selectorRepository.save(existingSelector);
            log.info("Added Selector {} for uuid {}", selector, uuid);
            return ResponseEntity.status(HttpStatus.OK).body("New selector " + selector + " added to the list");
        } catch (Exception e) {
            log.error("An error occurred while adding a new selector", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    public ResponseEntity<String> removeSelector(String uuid, Integer selectorIndex) {
        try {
            Selector existingSelector = selectorRepository.findByUuid(uuid);
            if (existingSelector == null) {
                log.error("Selector with id {} not found", uuid);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Selector with id " + uuid + " not found");
            }
            existingSelector.getSelectors().remove((int) selectorIndex);
            selectorRepository.save(existingSelector);
            log.info("Removed Selector {} for uuid {}", selectorIndex, uuid);
            return ResponseEntity.status(HttpStatus.OK).body("Deleted Selector " + selectorIndex);
        } catch (Exception e) {
            log.error("An error occurred while removing a selector", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
