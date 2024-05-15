package org.example.user.Controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.clients.dto.user.AddQueryRequest;
import org.example.user.Services.QueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@CrossOrigin(origins = "*")
@RequestMapping("api/users/query")
@RequiredArgsConstructor
public class QueryController {

    private final QueryService queryService;

    @GetMapping("/{id}/all")
    public ResponseEntity<List<String>> getAllQueries(@PathVariable String id) {
        return queryService.getAllQueries(id);
    }

    @PostMapping("/add")
    public ResponseEntity<String> addQuery(@RequestBody AddQueryRequest addQueryRequest) {
        String userId = addQueryRequest.getId();
        String query = addQueryRequest.getQuery();
        return queryService.addQuery(userId, query);
    }

    @DeleteMapping("/{id}/{index}/delete")
    public ResponseEntity<String> deleteQuery(@PathVariable String id, @PathVariable Integer index) {
        return queryService.deleteQuery(id, index);
    }
}
