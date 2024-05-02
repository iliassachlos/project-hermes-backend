package org.example.scraping.utils;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Slf4j
@Component
public class ImageUtil {
    public static boolean containsExcludedWords(String src, String alt) {
        List<String> excludedWords = List.of("logo", "svg", "avatar", "profile", "profiles", "webp", "gif");

        for (String word : excludedWords) {
            // Check if the source URL or the alt text contains the current word
            if (src.contains(word) || alt.contains(word)) {
                return true;
            }
        }
        return false;
    }
}
