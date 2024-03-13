package org.example.user.Controllers;

import lombok.RequiredArgsConstructor;
import org.example.user.Services.UserService;
import org.example.user.dto.RegisterUserRequest;
import org.example.user.dto.RegisterUserResponse;
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
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            return RegisterUserResponse.builder()
                    .message("Please provide all the required fields")
                    .build();
        } else {
            return userService.registerUser(username, email, password);
        }
    }

    //todo: login

    //todo: get all users

    //todo: get user by id
}
