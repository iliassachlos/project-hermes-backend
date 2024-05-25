package org.example.user.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.clients.Entities.User;
import org.example.user.Repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class QueryService {

    private final UserRepository userRepository;

    public ResponseEntity<List<String>> getAllQueries(String id) {
        try {
            User existingUser = userRepository.findUserById(id);
            if (existingUser == null) {
                log.error("User with ID {} was not found", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            List<String> queries = existingUser.getSavedQueries();
            log.info("Fetched all queries for user {}", id);
            return ResponseEntity.status(HttpStatus.OK).body(queries);
        } catch (Exception e) {
            log.error("An error occurred while getting saved queries", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<String> addQuery(String id, String query) {
        try {
            User existingUser = userRepository.findUserById(id);
            if (existingUser == null) {
                log.error("User with ID {} was not found", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with ID " + id + " was not found");
            }
            List<String> existingQueries = existingUser.getSavedQueries();
            existingQueries.add(query);
            existingUser.setSavedQueries(existingQueries);
            userRepository.save(existingUser);
            log.info("Query {} added successfully for user with id {}", query, id);
            return ResponseEntity.status(HttpStatus.OK).body("Query " + query + " added onUser with ID " + id + " added successfully");
        } catch (Exception e) {
            log.error("An error occurred while adding query", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<String> deleteQuery(String id, Integer index) {
        try {
            User existingUser = userRepository.findUserById(id);
            if (existingUser == null) {
                log.error("User with ID {} was not found", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with ID " + id + " was not found");
            }
            List<String> existingQueries = existingUser.getSavedQueries();
            existingQueries.remove(existingQueries.get(index));
            existingUser.setSavedQueries(existingQueries);
            userRepository.save(existingUser);
            log.info("Query {} deleted successfully for user with id {}", index, id);
            return ResponseEntity.status(HttpStatus.OK).body("Query deleted successfully for user with id " + id);
        } catch (Exception e) {
            log.error("An error occurred while deleting query", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
