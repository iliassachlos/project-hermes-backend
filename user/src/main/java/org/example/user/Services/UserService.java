package org.example.user.Services;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.user.Entities.User;
import org.example.user.Repositories.UserRepository;
import org.example.user.dto.LoginUserResponse;
import org.example.user.dto.RegisterUserResponse;
import org.example.user.dto.UserResponse;
import org.example.user.dto.UsersResponse;
import org.example.user.utilities.JwtTokenUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterUserResponse registerUser(String username, String email, String password) {
        User newUser = new User();
        String token = null;

        //Check if user credentials are not empty
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            return RegisterUserResponse.builder()
                    .message("Please provide all the required fields")
                    .build();
        }
        try {
            //Check if user already exists in database
            User existingUser = userRepository.findUserByEmail(email);
            if (existingUser != null) {
                log.error("User already exists");
                return RegisterUserResponse.builder()
                        .message("User already exists")
                        .build();
            }

            //Encrypt password
            String encryptedPassword = passwordEncoder.encode(password);

            //Build new user
            newUser = User.builder()
                    .username(username)
                    .email(email)
                    .password(encryptedPassword)
                    .isAdmin(false)
                    .build();

            //Save new user to database
            userRepository.save(newUser);

            // Generate JWT token
            token = JwtTokenUtil.generateToken(newUser.getId());
        } catch (Exception e) {
            log.error("Error occurred while registering user", e);
        }

        log.info("User created successfully {}", email);
        return RegisterUserResponse.builder()
                .message("User created successfully")
                .user(newUser)
                .token(token)
                .build();
    }

    public LoginUserResponse loginUser(String email, String password) {
        User foundUser = new User();
        String token = null;

        // Check if user credentials are not empty
        if (email.isEmpty() || password.isEmpty()) {
            log.error("Please provide all the required fields");
            return LoginUserResponse.builder()
                    .message("Please provide all the required fields")
                    .build();
        }
        try {
            // Check if user exists in the database
            foundUser = userRepository.findUserByEmail(email);
            if (foundUser == null) {
                log.error("User not found for email: {}", email);
                return LoginUserResponse.builder()
                        .message("User not found. Please check your credentials or register")
                        .build();
            }

            // Check found user's encrypted password with the provided password
            String encryptedPassword = foundUser.getPassword();
            if (passwordEncoder.matches(password, encryptedPassword)) {
                // Passwords match, login successful
                token = JwtTokenUtil.generateToken(foundUser.getId());
            } else {
                // Passwords don't match
                log.error("Invalid pasword. Please try again!");
                return LoginUserResponse.builder()
                        .message("Invalid password. Please try again!")
                        .build();
            }
        } catch (Exception e) {
            log.error("An error occurred while login user", e);
        }

        log.info("Login successful");
        return LoginUserResponse.builder()
                .message("Login successful")
                .user(foundUser)
                .token(token)
                .build();
    }

    public UsersResponse getAllUsers() {
        List<User> users = new ArrayList<>();
        try {
            users = userRepository.findAll();
        } catch (Exception e) {
            log.error("Error occurred while getting all users", e);
        }

        log.info("All users fetched successfully");
        return UsersResponse.builder()
                .users(users)
                .build();
    }


    public UserResponse getUserById(String id) {
        User user = new User();
        try {
            //Check if user exists in database
            user = userRepository.findUserById(id);
            if (user == null) {
                log.error("User was not found");
                return null;
            }
        } catch (Exception e) {
            log.error("Error occurred while finding user by id {}", id);
        }
        return UserResponse.builder()
                .user(user)
                .build();
    }
}

