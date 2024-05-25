package org.example.user.Controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.clients.Entities.User;
import org.example.clients.dto.user.*;
import org.example.user.Service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@CrossOrigin(origins = "*")
@RequestMapping("api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/status")
    public ResponseEntity<Boolean> checkUserServiceStatus() {
        log.info("Fetched service status");
        return ResponseEntity.status(HttpStatus.OK).body(true);
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponse> registerUser(@RequestBody RegisterUserRequest registerUserRequest) {
        String username = registerUserRequest.getUsername();
        String email = registerUserRequest.getEmail();
        String password = registerUserRequest.getPassword();
        return userService.registerUser(username, email, password);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginUserResponse> loginUser(@RequestBody LoginUserRequest loginUserRequest) {
        String email = loginUserRequest.getEmail();
        String password = loginUserRequest.getPassword();
        return userService.loginUser(email, password);
    }

    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        return userService.getUserById(id);
    }

    @GetMapping("/{id}/admin-status")
    public ResponseEntity<Boolean> isUserAdmin(@PathVariable String id) {
        return userService.isUserAdmin(id);
    }
}
