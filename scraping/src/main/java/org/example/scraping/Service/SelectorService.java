package org.example.scraping.Service;

import lombok.AllArgsConstructor;
import org.example.scraping.Entities.Selector;
import org.example.scraping.Repositories.SelectorRepository;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class SelectorService {
    private final SelectorRepository selectorRepository;

    public Selector addSelector(String id, String newSelector){
        Selector selector = selectorRepository.findById(id).orElse(null);
        if (selector != null) {
            selector.getSelectors().add(newSelector);
            selectorRepository.save(selector);
        }
        return selector;
    }


    public Selector removeSelector(String id, String selectorToRemove) {
        Selector selector = selectorRepository.findById(id).orElse(null);
        if (selector != null) {
            selector.getSelectors().remove(selectorToRemove);
            selectorRepository.save(selector);
        }
        return selector;
    }
}
