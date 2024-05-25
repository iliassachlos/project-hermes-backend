package org.example.user.Service;

import org.example.clients.dto.user.LoginUserResponse;
import org.mockito.Mockito;
import org.example.clients.Entities.User;
import org.example.clients.dto.user.RegisterUserResponse;
import org.example.user.Repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private User user;
    private User admin;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User(
                "id-1",
                "user@test.com",
                "testuser",
                "encrypted12345",
                false,
                new ArrayList<>(),
                new ArrayList<>()
        );
        admin = new User(
                "id-2",
                "admin@test.com",
                "adminuser",
                "encrypted12345",
                true,
                new ArrayList<>(),
                new ArrayList<>()
        );
    }

    @Test
    public void testRegisterUser_Success() {
        // Arrange
        String username = "testuser";
        String email = "user@test.com";
        String password = "password";

        Mockito.when(userRepository.findUserByEmail(email)).thenReturn(null);
        Mockito.when(passwordEncoder.encode(password)).thenReturn("encrypted12345");
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        //Act
        ResponseEntity<RegisterUserResponse> response = userService.registerUser(username, email, password);
        response.getBody().getUser().setId("id-1");

        //Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("User created successfully", response.getBody().getMessage());
        assertEquals(user, response.getBody().getUser());
    }

    @Test
    public void testRegisterUser_EmptyFields() {
        //Arrange
        String username = "";
        String email = "";
        String password = "";

        //Act
        ResponseEntity<RegisterUserResponse> response = userService.registerUser(username, email, password);

        //Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Please provide all the required fields", response.getBody().getMessage());
    }

    @Test
    public void testRegisterUser_UserAlreadyExists() {
        // Arrange
        String username = "testuser";
        String email = "user@test.com";
        String password = "password";

        Mockito.when(userRepository.findUserByEmail(email)).thenReturn(user);
        Mockito.when(passwordEncoder.encode(password)).thenReturn("encrypted12345");
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        //Act
        ResponseEntity<RegisterUserResponse> response = userService.registerUser(username, email, password);

        //Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("User already exists", response.getBody().getMessage());
    }

    @Test
    public void testRegisterUser_InternalServerError() {
        //Arrange
        String username = "testuser";
        String email = "user@test.com";
        String password = "password";

        Mockito.when(userRepository.findUserByEmail(email)).thenThrow(new RuntimeException("Error occurred while registering user"));

        //Act
        ResponseEntity<RegisterUserResponse> response = userService.registerUser(username, email, password);

        //Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error occurred while registering user", response.getBody().getMessage());
    }

    @Test
    public void testLoginUser_Success() {
        //Arrange
        String email = "admin@test.com";
        String password = "12345";

        Mockito.when(userRepository.findUserByEmail(email)).thenReturn(user);
        Mockito.when(passwordEncoder.matches(password, user.getPassword())).thenReturn(true);

        //Act
        ResponseEntity<LoginUserResponse> response = userService.loginUser(email, password);

        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Login successful", response.getBody().getMessage());
        assertEquals(user, response.getBody().getUser());
    }

    @Test
    public void testLoginUser_UserNotFound() {
        //Arrange
        String email = "user@test.com";
        String password = "12345";

        Mockito.when(userRepository.findUserByEmail(email)).thenReturn(null);

        //Act
        ResponseEntity<LoginUserResponse> response = userService.loginUser(email, password);
        System.out.println(response);
        //Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User was not found. Please check your credentials or register", response.getBody().getMessage());
    }

    @Test
    public void testLoginUser_EmptyFields() {
        //Arrange
        String email = "";
        String password = "";

        //Act
        ResponseEntity<LoginUserResponse> response = userService.loginUser(email, password);

        //Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Please provide all the required credentials", response.getBody().getMessage());
    }

    @Test
    public void testLoginUser_InvalidPassword() {
        //Arrange
        String email = "user@test.com";
        String password = "12345";

        Mockito.when(userRepository.findUserByEmail(email)).thenReturn(user);
        Mockito.when(passwordEncoder.matches(password, user.getPassword())).thenReturn(false);

        //Act
        ResponseEntity<LoginUserResponse> response = userService.loginUser(email, password);

        //Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid password. Please try again!", response.getBody().getMessage());
    }

    @Test
    public void testLoginUser_InternalServerError() {
        //Arrange
        String email = "user@test.com";
        String password = "12345";

        Mockito.when(userRepository.findUserByEmail(email)).thenThrow(new RuntimeException("An error occurred while login user"));

        //Act
        ResponseEntity<LoginUserResponse> response = userService.loginUser(email, password);

        //Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An error occurred while login user", response.getBody().getMessage());
    }

    @Test
    public void testGetAllUsers_Success() {
        //Arrange
        List<User> users = new ArrayList<>();
        users.add(user);
        Mockito.when(userRepository.findAll()).thenReturn(users);

        //Act
        ResponseEntity<List<User>> response = userService.getAllUsers();
        System.out.println(response);

        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(users, response.getBody());
    }

    @Test
    public void testGetAllUsers_InternalServerError() {
        //Arrange
        Mockito.when(userRepository.findAll()).thenThrow(new RuntimeException("An error occurred while login user"));

        //Act
        ResponseEntity<List<User>> response = userService.getAllUsers();

        //Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void testGetUserById_Success() {
        //Arrange
        String id = "id-1";

        Mockito.when(userRepository.findUserById(id)).thenReturn(user);

        //Act
        ResponseEntity<User> response = userService.getUserById(id);

        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
        assertEquals(id, response.getBody().getId());
    }

    @Test
    public void testGetUserById_UserNotFound() {
        //Arrange
        String id = "id-1";

        Mockito.when(userRepository.findUserById(id)).thenReturn(null);

        //Act
        ResponseEntity<User> response = userService.getUserById(id);

        //Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotEquals(user, response.getBody());
    }

    @Test
    public void testGetUserById_InternalServerError() {
        //Arrange
        String id = "id-1";

        Mockito.when(userRepository.findUserById(id)).thenThrow(new RuntimeException("Error occurred while finding user by id " + id));

        //Act
        ResponseEntity<User> response = userService.getUserById(id);

        //Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotEquals(user, response.getBody());
    }

    @Test
    public void testIsUserAdmin_SuccessTrue() {
        //Arrange
        String id = "id-2";

        Mockito.when(userRepository.findUserById(id)).thenReturn(admin);

        //Act
        ResponseEntity<Boolean> response = userService.isUserAdmin(id);

        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, response.getBody());
    }

    @Test
    public void testIsUserAdmin_SuccessFalse() {
        //Arrange
        String id = "id-1";

        Mockito.when(userRepository.findUserById(id)).thenReturn(user);

        //Act
        ResponseEntity<Boolean> response = userService.isUserAdmin(id);

        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(false, response.getBody());
    }

    @Test
    public void testIsAdminUser_InternalServerError() {
        //Arrange
        String id = "id-1";

        Mockito.when(userRepository.findUserById(id)).thenThrow(new RuntimeException("Error occurred while finding user by id " + id));

        //Act
        ResponseEntity<Boolean> response = userService.isUserAdmin(id);

        //Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
