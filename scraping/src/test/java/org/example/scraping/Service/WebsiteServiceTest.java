package org.example.scraping.Service;

import org.example.scraping.Entities.Website;
import org.example.scraping.Repositories.WebsitesRepository;
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
import java.util.HashMap;
import java.util.List;

public class WebsiteServiceTest {

    @InjectMocks
    private WebsiteService websiteService;

    @Mock
    private WebsitesRepository websitesRepository;

    private Website website;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        HashMap<String, String> categories = new HashMap<>();
        categories.put("politics", "https://website.com/politics");
        categories.put("economy", "https://website.com/economy");
        website = Website.builder()
                .uuid("uuid-1")
                .title("website-1")
                .icon("https://website.com/icon1")
                .value("website-1")
                .categories(categories)
                .build();
    }

    @Test
    public void testGetAllWebsites_Success() {
        //Arrange
        List<Website> websites = new ArrayList<>();
        websites.add(website);

        Mockito.when(websitesRepository.findAll()).thenReturn(websites);

        //Act
        ResponseEntity<List<Website>> response = websiteService.getAllWebsites();

        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(websites, response.getBody());
        assertEquals(1, response.getBody().size());

        Mockito.verify(websitesRepository, Mockito.times(1)).findAll();
    }

    @Test
    public void testGetAllWebsites_WebsitesNotFound() {
        //Arrange
        List<Website> websites = new ArrayList<>();

        Mockito.when(websitesRepository.findAll()).thenReturn(websites);

        //Act
        ResponseEntity<List<Website>> response = websiteService.getAllWebsites();

        //Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        Mockito.verify(websitesRepository, Mockito.times(1)).findAll();
    }

    @Test
    public void testGetAllWebsites_InternalServerError() {
        //Arrange
        Mockito.when(websitesRepository.findAll()).thenThrow(new RuntimeException());

        //Act
        ResponseEntity<List<Website>> response = websiteService.getAllWebsites();

        //Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void testGetWebsiteByUuid_Success() {
        //Arrange
        String uuid = "uuid-1";

        Mockito.when(websitesRepository.findByUuid(uuid)).thenReturn(website);

        //Act
        ResponseEntity<Website> response = websiteService.getWebsiteByUUID(uuid);

        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(website, response.getBody());
        assertEquals(uuid, response.getBody().getUuid());

        Mockito.verify(websitesRepository, Mockito.times(1)).findByUuid(uuid);
    }

    @Test
    public void testGetWebsiteByUuid_WebsiteNotFound() {
        //Arrange
        String uuid = "uuid-1";

        Mockito.when(websitesRepository.findByUuid(uuid)).thenReturn(null);

        //Act
        ResponseEntity<Website> response = websiteService.getWebsiteByUUID(uuid);

        //Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

        Mockito.verify(websitesRepository, Mockito.times(1)).findByUuid(uuid);
    }

    @Test
    public void testGetWebsiteByUuid_InternalServerError() {
        //Arrange
        String uuid = "uuid-1";
        Mockito.when(websitesRepository.findByUuid(uuid)).thenThrow(new RuntimeException());

        //Act
        ResponseEntity<Website> response = websiteService.getWebsiteByUUID(uuid);

        //Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());

        Mockito.verify(websitesRepository, Mockito.times(1)).findByUuid(uuid);
    }

    @Test
    public void testSaveWebsite_Success() {
        //Arrange
        String title = "website-1";
        String icon = "https://website.com/icon1";
        String value = "website-1";
        Website newWebsite = Website.builder()
                .uuid("uuid-2")
                .title(title)
                .icon(icon)
                .value(value)
                .categories(new HashMap<>())
                .build();

        Mockito.when(websitesRepository.save(Mockito.any(Website.class))).thenReturn(newWebsite);

        // Act
        ResponseEntity<Website> response = websiteService.saveWebsite(title, icon, value);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(newWebsite, response.getBody());
        assertEquals(title, response.getBody().getTitle());
        assertEquals(icon, response.getBody().getIcon());
        assertEquals(value, response.getBody().getValue());

        Mockito.verify(websitesRepository, Mockito.times(1)).save(Mockito.any(Website.class));
    }

    @Test
    public void testSaveWebsite_EmptyFields() {
        //Arrange
        String title = null;
        String icon = "";
        String value = "website-1";

        //Act
        ResponseEntity<Website> response = websiteService.saveWebsite(title, icon, value);

        //Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());

        Mockito.verify(websitesRepository, Mockito.times(0)).save(Mockito.any(Website.class));
    }

    @Test
    public void testSaveWebsite_InternalServerError() {
        //Arrange
        String title = "website-1";
        String icon = "https://website.com/icon1";
        String value = "website-1";
        Website newWebsite = Website.builder()
                .uuid("uuid-2")
                .title(title)
                .icon(icon)
                .value(value)
                .categories(new HashMap<>())
                .build();

        Mockito.when(websitesRepository.save(Mockito.any(Website.class))).thenThrow(new RuntimeException());

        //Act
        ResponseEntity<Website> response = websiteService.saveWebsite(title, icon, value);

        //Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());

        Mockito.verify(websitesRepository, Mockito.times(1)).save(Mockito.any(Website.class));
    }

    @Test
    public void testEditWebsite_Success() {
        //Arrange
        String id = "uuid-1";
        String title = "editedTitle";
        String icon = "editedIcon";
        String value = "editedValue";

        Mockito.when(websitesRepository.findByUuid(id)).thenReturn(website);
        Mockito.when(websitesRepository.save(website)).thenReturn(website);

        //Act
        ResponseEntity<Website> response = websiteService.editWebsite(id, title, icon, value);

        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(title, response.getBody().getTitle());
        assertEquals(icon, response.getBody().getIcon());
        assertEquals(value, response.getBody().getValue());

        Mockito.verify(websitesRepository, Mockito.times(1)).findByUuid(id);
        Mockito.verify(websitesRepository, Mockito.times(1)).save(website);
    }

    @Test
    public void testEditWebsite_EmptyValue() {
        //Arrange
        String id = "uuid-1";
        String title = "";
        String icon = "editedIcon";
        String value = null;

        //Act
        ResponseEntity<Website> response = websiteService.editWebsite(id, title, icon, value);

        //Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());

        Mockito.verify(websitesRepository, Mockito.times(0)).findByUuid(id);
        Mockito.verify(websitesRepository, Mockito.times(0)).save(website);
    }

    @Test
    public void testEditWebsite_WebsiteNotFound() {
        //Arrange
        String id = "uuid-1";
        String title = "editedTitle";
        String icon = "editedIcon";
        String value = "editedValue";

        Mockito.when(websitesRepository.findByUuid(id)).thenReturn(null);

        //Act
        ResponseEntity<Website> response = websiteService.editWebsite(id, title, icon, value);

        //Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

        Mockito.verify(websitesRepository, Mockito.times(1)).findByUuid(id);
        Mockito.verify(websitesRepository, Mockito.times(0)).save(website);
    }

    @Test
    public void testEditWebsite_InternalServerError() {
        //Arrange
        String id = "uuid-1";
        String title = "editedTitle";
        String icon = "editedIcon";
        String value = "editedValue";

        Mockito.when(websitesRepository.findByUuid(id)).thenThrow(new RuntimeException());

        //Act
        ResponseEntity<Website> response = websiteService.editWebsite(id, title, icon, value);

        //Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());

        Mockito.verify(websitesRepository, Mockito.times(1)).findByUuid(id);
        Mockito.verify(websitesRepository, Mockito.times(0)).save(website);
    }

    @Test
    public void testDeleteWebsite_Success() {
        // Arrange
        String id = "uuid-1";

        Mockito.when(websitesRepository.findByUuid(id)).thenReturn(website);
        Mockito.doNothing().when(websitesRepository).delete(website);

        // Act
        ResponseEntity<String> response = websiteService.deleteWebsite(id);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Website: " + id + " was successfully deleted", response.getBody());

        Mockito.verify(websitesRepository, Mockito.times(1)).findByUuid(id);
        Mockito.verify(websitesRepository, Mockito.times(1)).delete(website);
    }


    @Test
    public void testDeleteWebsite_WebsiteNotFound() {
        // Arrange
        String id = "uuid-1";

        Mockito.when(websitesRepository.findByUuid(id)).thenReturn(null);
        Mockito.doNothing().when(websitesRepository).delete(website);

        // Act
        ResponseEntity<String> response = websiteService.deleteWebsite(id);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Website with title " + id + " was not found", response.getBody());

        Mockito.verify(websitesRepository, Mockito.times(1)).findByUuid(id);
        Mockito.verify(websitesRepository, Mockito.times(0)).delete(website);
    }

    @Test
    public void testDeleteWebsite_InternalServerError() {
        //Arrange
        String id = "uuid-1";

        Mockito.when(websitesRepository.findByUuid(id)).thenThrow(new RuntimeException());
        Mockito.doNothing().when(websitesRepository).delete(website);

        //Act
        ResponseEntity<String> response = websiteService.deleteWebsite(id);

        //Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());

        Mockito.verify(websitesRepository, Mockito.times(1)).findByUuid(id);
        Mockito.verify(websitesRepository, Mockito.times(0)).delete(website);
    }

    @Test
    public void testSaveWebsiteCategory_Success() {
        //Arrange
        String id = "uuid-1";
        String category = "category-1";
        String url = "www.example.com/url";

        Mockito.when(websitesRepository.findByUuid(id)).thenReturn(website);
        Mockito.when(websitesRepository.save(website)).thenReturn(website);

        //Act
        ResponseEntity<Website> response = websiteService.saveWebsiteCategory(id, category, url);

        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(website, response.getBody());

        Mockito.verify(websitesRepository, Mockito.times(1)).findByUuid(id);
        Mockito.verify(websitesRepository, Mockito.times(1)).save(website);
    }

    @Test
    public void testSaveWebsiteCategory_EmptyFields() {
        //Arrange
        String id = null;
        String category = "";
        String url = "www.example.com/url";

        Mockito.when(websitesRepository.findByUuid(id)).thenReturn(website);
        Mockito.when(websitesRepository.save(website)).thenReturn(website);

        //Act
        ResponseEntity<Website> response = websiteService.saveWebsiteCategory(id, category, url);

        //Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());

        Mockito.verify(websitesRepository, Mockito.times(0)).findByUuid(id);
        Mockito.verify(websitesRepository, Mockito.times(0)).save(website);
    }

    @Test
    public void testSaveWebsiteCategory_WebsiteNotFound() {
        //Arrange
        String id = "uuid-1";
        String category = "category-1";
        String url = "www.example.com/url";

        Mockito.when(websitesRepository.findByUuid(id)).thenReturn(null);
        Mockito.when(websitesRepository.save(website)).thenReturn(website);

        //Act
        ResponseEntity<Website> response = websiteService.saveWebsiteCategory(id, category, url);

        //Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

        Mockito.verify(websitesRepository, Mockito.times(1)).findByUuid(id);
        Mockito.verify(websitesRepository, Mockito.times(0)).save(website);
    }

    @Test
    public void testSaveWebsiteCategory_InternalServerError() {
        //Arrange
        String id = "uuid-1";
        String category = "category-1";
        String url = "www.example.com/url";

        Mockito.when(websitesRepository.findByUuid(id)).thenThrow(new RuntimeException());

        //Act
        ResponseEntity<Website> response = websiteService.saveWebsiteCategory(id, category, url);

        //Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());

        Mockito.verify(websitesRepository, Mockito.times(1)).findByUuid(id);
        Mockito.verify(websitesRepository, Mockito.times(0)).save(website);
    }

    @Test
    public void testDeleteWebsiteCategory_Success() {
        //Arrange
        String id = "uuid-1";
        String categoryToDelete = "category-1";

        Mockito.when(websitesRepository.findByUuid(id)).thenReturn(website);
        Mockito.when(websitesRepository.save(website)).thenReturn(website);

        //Act
        ResponseEntity<Website> response = websiteService.deleteWebsiteCategory(id, categoryToDelete);

        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(website, response.getBody());

        Mockito.verify(websitesRepository, Mockito.times(1)).findByUuid(id);
        Mockito.verify(websitesRepository, Mockito.times(1)).save(website);
    }

    @Test
    public void testDeleteWebsiteCategory_EmptyFields() {
        //Arrange
        String id = null;
        String categoryToDelete = "";

        Mockito.when(websitesRepository.findByUuid(id)).thenReturn(website);
        Mockito.when(websitesRepository.save(website)).thenReturn(website);

        //Act
        ResponseEntity<Website> response = websiteService.deleteWebsiteCategory(id, categoryToDelete);

        //Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());

        Mockito.verify(websitesRepository, Mockito.times(0)).findByUuid(id);
        Mockito.verify(websitesRepository, Mockito.times(0)).save(website);
    }

    @Test
    public void testDeleteWebsiteCategory_WebsiteNotFound() {
        //Arrange
        String id = "uuid-1";
        String categoryToDelete = "category-1";

        Mockito.when(websitesRepository.findByUuid(id)).thenReturn(null);
        Mockito.when(websitesRepository.save(website)).thenReturn(website);

        //Act
        ResponseEntity<Website> response = websiteService.deleteWebsiteCategory(id, categoryToDelete);

        //Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

        Mockito.verify(websitesRepository, Mockito.times(1)).findByUuid(id);
        Mockito.verify(websitesRepository, Mockito.times(0)).save(website);
    }

    @Test
    public void testDeleteWebsiteCategory_InternalServerError() {
        //Arrange
        String id = "uuid-1";
        String categoryToDelete = "category-1";

        Mockito.when(websitesRepository.findByUuid(id)).thenThrow(new RuntimeException());

        //Act
        ResponseEntity<Website> response = websiteService.deleteWebsiteCategory(id, categoryToDelete);

        //Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());

        Mockito.verify(websitesRepository, Mockito.times(1)).findByUuid(id);
        Mockito.verify(websitesRepository, Mockito.times(0)).save(website);
    }
}
