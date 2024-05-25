package org.example.scraping.Service;

import org.example.scraping.Entities.Selector;
import org.example.scraping.Repositories.SelectorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

public class SelectorServiceTest {

    @InjectMocks
    private SelectorService selectorService;

    @Mock
    private SelectorRepository selectorRepository;

    private Selector selector;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        List<String> selectors = new ArrayList<>();
        selectors.add("h2");
        selectors.add("div.test");
        selector = Selector.builder()
                .id("id-1")
                .uuid("uuid-1")
                .name("selector-1")
                .selectors(selectors)
                .build();
    }

    @Test
    public void testGetAllSelectors_Success() {
        //Arrange
        List<Selector> selectors = new ArrayList<>();
        selectors.add(selector);

        Mockito.when(selectorRepository.findAll()).thenReturn(selectors);

        //Act
        ResponseEntity<List<Selector>> response = selectorService.getAllSelectors();

        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(selectors, response.getBody());
        assertEquals(1, response.getBody().size());

        Mockito.verify(selectorRepository, Mockito.times(1)).findAll();
    }

    @Test
    public void testGetAllSelectors_SelectorsNotFound() {
        //Arrange
        List<Selector> selectors = new ArrayList<>();

        Mockito.when(selectorRepository.findAll()).thenReturn(selectors);

        //Act
        ResponseEntity<List<Selector>> response = selectorService.getAllSelectors();

        //Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

        Mockito.verify(selectorRepository, Mockito.times(1)).findAll();
    }

    @Test
    public void testGetAllSelectors_InternalServerError() {
        //Arrange
        List<Selector> selectors = new ArrayList<>();
        selectors.add(selector);

        Mockito.when(selectorRepository.findAll()).thenThrow(new RuntimeException());

        //Act
        ResponseEntity<List<Selector>> response = selectorService.getAllSelectors();

        //Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());

        Mockito.verify(selectorRepository, Mockito.times(1)).findAll();
    }

    @Test
    public void testGetSelectorByUuid_Success() {
        //Arrange
        String uuid = "uuid-1";

        Mockito.when(selectorRepository.findByUuid(uuid)).thenReturn(selector);

        //Act
        ResponseEntity<Selector> response = selectorService.getSelectorByUuid(uuid);

        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(selector, response.getBody());

        Mockito.verify(selectorRepository, Mockito.times(1)).findByUuid(uuid);
    }

    @Test
    public void testGetSelectorByUuid_SelectorNotFound() {
        //Arrange
        String uuid = "uuid-1";

        Mockito.when(selectorRepository.findByUuid(uuid)).thenReturn(null);

        //Act
        ResponseEntity<Selector> response = selectorService.getSelectorByUuid(uuid);

        //Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

        Mockito.verify(selectorRepository, Mockito.times(1)).findByUuid(uuid);
    }

    @Test
    public void testGetSelectorByUuid_InternalServerError() {
        //Arrange
        String uuid = "uuid-1";

        Mockito.when(selectorRepository.findByUuid(uuid)).thenThrow(new RuntimeException());

        //Act
        ResponseEntity<Selector> response = selectorService.getSelectorByUuid(uuid);

        //Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());

        Mockito.verify(selectorRepository, Mockito.times(1)).findByUuid(uuid);
    }

    @Test
    public void testAddSelector_Success(){
        //Arrange
        String uuid = "uuid-1";
        String newSelector = "h2";

        Mockito.when(selectorRepository.findByUuid(uuid)).thenReturn(selector);
        Mockito.when(selectorRepository.save(selector)).thenReturn(selector);

        //Act
        ResponseEntity<String> response = selectorService.addSelector(uuid, newSelector);

        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("New selector " + newSelector + " added to the list", response.getBody());

        Mockito.verify(selectorRepository, Mockito.times(1)).findByUuid(uuid);
        Mockito.verify(selectorRepository, Mockito.times(1)).save(selector);

    }

    @Test
    public void testAddSelector_SelectorNotFound(){
        //Arrange
        String uuid = "uuid-1";
        String newSelector = "h2";

        Mockito.when(selectorRepository.findByUuid(uuid)).thenReturn(null);

        //Act
        ResponseEntity<String> response = selectorService.addSelector(uuid, newSelector);

        //Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Selector with uuid " + uuid + " not found", response.getBody());

        Mockito.verify(selectorRepository, Mockito.times(1)).findByUuid(uuid);
        Mockito.verify(selectorRepository, Mockito.times(0)).save(selector);
    }

    @Test
    public void testAddSelector_InternalServerError(){
        //Arrange
        String uuid = "uuid-1";
        String newSelector = "h2";

        Mockito.when(selectorRepository.findByUuid(uuid)).thenThrow(new RuntimeException());

        //Act
        ResponseEntity<String> response = selectorService.addSelector(uuid, newSelector);

        //Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());

        Mockito.verify(selectorRepository, Mockito.times(1)).findByUuid(uuid);
        Mockito.verify(selectorRepository, Mockito.times(0)).save(selector);
    }

    @Test
    public void testRemoveSelector_Success(){
        //Arrange
        String uuid = "uuid-1";
        Integer selectorIndex = 1;

        Mockito.when(selectorRepository.findByUuid(uuid)).thenReturn(selector);
        Mockito.when(selectorRepository.save(selector)).thenReturn(selector);

        //Act
        ResponseEntity<String> response = selectorService.removeSelector(uuid, selectorIndex);

        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Deleted Selector " + selectorIndex, response.getBody());

        Mockito.verify(selectorRepository, Mockito.times(1)).findByUuid(uuid);
        Mockito.verify(selectorRepository, Mockito.times(1)).save(selector);
    }

    @Test
    public void testRemoveSelector_SelectorNotFound(){
        //Arrange
        String uuid = "uuid-1";
        Integer selectorIndex = 1;

        Mockito.when(selectorRepository.findByUuid(uuid)).thenReturn(null);
        Mockito.when(selectorRepository.save(selector)).thenReturn(selector);

        //Act
        ResponseEntity<String> response = selectorService.removeSelector(uuid, selectorIndex);

        //Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Selector with uuid " + uuid + " not found", response.getBody());

        Mockito.verify(selectorRepository, Mockito.times(1)).findByUuid(uuid);
        Mockito.verify(selectorRepository, Mockito.times(0)).save(selector);
    }

    @Test
    public void testRemoveSelector_invalidSelectorIndex(){
        //Arrange
        String uuid = "uuid-1";
        Integer selectorIndex = 10000;

        Mockito.when(selectorRepository.findByUuid(uuid)).thenReturn(selector);
        Mockito.when(selectorRepository.save(selector)).thenReturn(selector);

        //Act
        ResponseEntity<String> response = selectorService.removeSelector(uuid, selectorIndex);

        //Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());

        Mockito.verify(selectorRepository, Mockito.times(1)).findByUuid(uuid);
        Mockito.verify(selectorRepository, Mockito.times(0)).save(selector);
    }

    @Test
    public void testRemoveSelector_InternalServerError(){
        //Arrange
        String uuid = "uuid-1";
        Integer selectorIndex = 2;

        Mockito.when(selectorRepository.findByUuid(uuid)).thenThrow(new RuntimeException());

        //Act
        ResponseEntity<String> response = selectorService.removeSelector(uuid, selectorIndex);

        //Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());

        Mockito.verify(selectorRepository, Mockito.times(1)).findByUuid(uuid);
        Mockito.verify(selectorRepository, Mockito.times(0)).save(selector);
    }
}
