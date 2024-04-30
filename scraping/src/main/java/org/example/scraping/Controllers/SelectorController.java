package org.example.scraping.Controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.scraping.Entities.Selector;
import org.example.scraping.Service.SelectorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@CrossOrigin(origins = "*")
@RequestMapping("api/scraping/selector")
@RequiredArgsConstructor
public class SelectorController {

    private final SelectorService selectorService;

    @GetMapping("/selectors")
    public ResponseEntity<List<Selector>> getAllSelectors() {
        return selectorService.getAllSelectors();
    }

    @PostMapping("/{id}/add")
    public ResponseEntity<Selector> addSelector(@PathVariable String id, @RequestBody String newSelector) {
        return selectorService.addSelector(id, newSelector);
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Selector> deleteSelectorByName(@PathVariable String id, @RequestBody String selectorToRemove) {
        return selectorService.removeSelector(id, selectorToRemove);
    }
}


