package org.example.user.Controllers;

import lombok.RequiredArgsConstructor;
import org.example.user.Services.UserService;
import org.example.user.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterUserResponse registerUser(@RequestBody RegisterUserRequest registerUserRequest) {
        String username = registerUserRequest.getUsername();
        String email = registerUserRequest.getEmail();
        String password = registerUserRequest.getPassword();
        return userService.registerUser(username, email, password);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.FOUND)
    public LoginUserResponse loginUser(@RequestBody LoginUserRequest loginUserRequest) {
        String email = loginUserRequest.getEmail();
        String password = loginUserRequest.getPassword();
        return userService.loginUser(email,password);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public UsersResponse getAllUsers(){
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse getUserById(@PathVariable String id){
        return userService.getUserById(id);
    }
}
