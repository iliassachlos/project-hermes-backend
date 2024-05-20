package org.example.user.Services;

import org.example.clients.Entities.User;
import org.example.user.Repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

public class QueryServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private QueryService queryService;

    private User user;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        List<String> queries = new ArrayList<>();
        queries.add("query1");
        queries.add("query2");

        user = new User(
                "user123",
                "user123@test.com",
                "user123",
                "12345",
                false,
                new ArrayList<>(),
                queries
        );
    }

    @Test
    public void testGetAllQueries_Success() {
        // Arrange
        String userId = "user123";
        when(userRepository.findUserById(userId)).thenReturn(user);

        // Act
        ResponseEntity<List<String>> responseEntity = queryService.getAllQueries(userId);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(user.getSavedQueries(), responseEntity.getBody());
    }

    @Test
    public void testGetAllQueries_UserNotFound() {
        // Arrange
        String userId = "user-200";
        when(userRepository.findUserById(userId)).thenReturn(null);

        // Act
        ResponseEntity<List<String>> responseEntity = queryService.getAllQueries(userId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    public void testAddQuery_Success() {
        // Arrange
        String userId = "user123";
        String query = "newQuery";
        when(userRepository.findUserById(userId)).thenReturn(user);

        // Act
        ResponseEntity<String> responseEntity = queryService.addQuery(userId, query);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Query " + query + " added onUser with ID " + userId + " added successfully", responseEntity.getBody());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testAddQuery_UserNotFound() {
        // Arrange
        String userId = "user-200";
        String query = "newQuery";
        when(userRepository.findUserById(userId)).thenReturn(null);

        // Act
        ResponseEntity<String> responseEntity = queryService.addQuery(userId, query);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("User with ID " + userId + " was not found", responseEntity.getBody());
    }

    @Test
    public void testDeleteQuery_Success() {
        // Arrange
        String userId = "user123";
        Integer index = 0;
        when(userRepository.findUserById(userId)).thenReturn(user);

        // Act
        ResponseEntity<String> responseEntity = queryService.deleteQuery(userId, index);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Query deleted successfully for user with id " + userId, responseEntity.getBody());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testDeleteQuery_UserNotFound() {
        // Arrange
        String userId = "user-200";
        Integer index = 0;
        when(userRepository.findUserById(userId)).thenReturn(null);

        // Act
        ResponseEntity<String> responseEntity = queryService.deleteQuery(userId, index);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("User with ID " + userId + " was not found", responseEntity.getBody());
    }

    @Test
    public void testDeleteQuery_InternalServerError() {
        // Arrange
        String userId = "user123";
        Integer index = 0;
        when(userRepository.findUserById(userId)).thenThrow(new RuntimeException("Database connection error"));

        // Act
        ResponseEntity<String> responseEntity = queryService.deleteQuery(userId, index);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }
}
