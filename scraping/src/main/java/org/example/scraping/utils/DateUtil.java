package org.example.scraping.utils;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Data
@Slf4j
@Component
public class DateUtil {
    public static boolean isArticleOlderThanThreeDays(String time) {
        LocalDate threeDaysAgo = LocalDate.now().minusDays(3);
        String dateString = time.split("T")[0]; // Extract the date part from the timestamp
        DateTimeFormatter[] formatters = {
                DateTimeFormatter.ofPattern("yyyy-MM-dd"),
                DateTimeFormatter.ofPattern("dd-MM-yyyy"),
        };

        for (DateTimeFormatter formatter : formatters) {
            try {
                LocalDate articleDate = LocalDate.parse(dateString, formatter);
                if (articleDate.isAfter(threeDaysAgo)) {
                    return true;
                }
            } catch (DateTimeParseException e) {
                log.error("Invalid date format: {}", dateString);
            }

        }
        return false;
    }
}
