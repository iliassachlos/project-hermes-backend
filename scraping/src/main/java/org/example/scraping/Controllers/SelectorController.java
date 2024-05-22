package org.example.scraping.Controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.scraping.Entities.Selector;
import org.example.scraping.Service.SelectorService;
import org.example.scraping.dto.AddSelectorRequest;
import org.example.scraping.dto.DeleteSelectorRequest;
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

    @GetMapping("/all")
    public ResponseEntity<List<Selector>> getAllSelectors() {
        return selectorService.getAllSelectors();
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<Selector> getSelector(@PathVariable String uuid) {
        return selectorService.getSelectorByUuid(uuid);
    }

    @PostMapping("/{uuid}/add")
    public ResponseEntity<String> addSelector(@PathVariable String uuid, @RequestBody AddSelectorRequest addSelectorRequest) {
        String selector = addSelectorRequest.getSelector();
        return selectorService.addSelector(uuid, selector);
    }

    @PutMapping("/{uuid}/delete")
    public ResponseEntity<String> deleteSelectorByName(@PathVariable String uuid, @RequestBody DeleteSelectorRequest deleteSelectorRequest) {
        Integer selectorIndex = deleteSelectorRequest.getSelectorIndex();
        return selectorService.removeSelector(uuid, selectorIndex);
    }
}


