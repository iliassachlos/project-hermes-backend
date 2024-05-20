package org.example.user.Services;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.clients.Entities.User;
import org.example.user.Repositories.UserRepository;
import org.example.clients.dto.user.LoginUserResponse;
import org.example.clients.dto.user.RegisterUserResponse;
import org.example.user.utilities.JwtTokenUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ResponseEntity<RegisterUserResponse> registerUser(String username, String email, String password) {
        //Check if user credentials are not empty
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            log.error("Please provide all the required fields");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    RegisterUserResponse.builder()
                            .message("Please provide all the required fields")
                            .build()
            );
        }
        try {
            //Check if user already exists in database
            User existingUser = userRepository.findUserByEmail(email);
            if (existingUser != null) {
                log.error("User already exists");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(
                        RegisterUserResponse.builder()
                                .message("User already exists")
                                .build()
                );
            }

            //Encrypt password
            String encryptedPassword = passwordEncoder.encode(password);

            //Build new user
            User newUser = User.builder()
                    .username(username)
                    .email(email)
                    .password(encryptedPassword)
                    .isAdmin(false)
                    .bookmarkedArticles(new ArrayList<>())
                    .savedQueries(new ArrayList<>())
                    .build();

            //Save new user to database
            userRepository.save(newUser);

            // Generate JWT token
            String token = JwtTokenUtil.generateToken(newUser.getId());
            log.info("User created successfully {}", email);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    RegisterUserResponse.builder()
                            .message("User created successfully")
                            .user(newUser)
                            .token(token)
                            .build()
            );
        } catch (Exception e) {
            log.error("Error occurred while registering user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    RegisterUserResponse.builder()
                            .message("Error occurred while registering user")
                            .build()
            );
        }
    }

    public ResponseEntity<LoginUserResponse> loginUser(String email, String password) {
        // Check if user credentials are not empty
        if (email.isEmpty() || password.isEmpty()) {
            log.error("Please provide all the required fields");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    LoginUserResponse.builder()
                            .message("Please provide all the required credentials")
                            .build()
            );
        }
        try {
            // Check if user exists in the database
            User foundUser = userRepository.findUserByEmail(email);
            if (foundUser == null) {
                log.error("User not found for email: {}", email);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        LoginUserResponse.builder()
                                .message("User was not found. Please check your credentials or register")
                                .build()
                );
            }

            // Check found user's encrypted password with the provided password
            String encryptedPassword = foundUser.getPassword();
            if (passwordEncoder.matches(password, encryptedPassword)) {
                // Passwords match, login successful
                String token = JwtTokenUtil.generateToken(foundUser.getId());

                log.info("Login successful");
                return ResponseEntity.status(HttpStatus.OK).body(
                        LoginUserResponse.builder()
                                .message("Login successful")
                                .user(foundUser)
                                .token(token)
                                .build()
                );
            } else {
                // Passwords don't match
                log.error("Invalid password. Please try again!");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        LoginUserResponse.builder()
                                .message("Invalid password. Please try again!")
                                .build()
                );
            }
        } catch (Exception e) {
            log.error("An error occurred while login user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    LoginUserResponse.builder()
                            .message("An error occurred while login user")
                            .build()
            );
        }
    }

    public ResponseEntity<List<User>> getAllUsers() {
        try {
            List<User> users = userRepository.findAll();
            log.info("All users fetched successfully");
            return ResponseEntity.status(HttpStatus.OK).body(users);
        } catch (Exception e) {
            log.error("Error occurred while getting all users", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<User> getUserById(String id) {
        try {
            //Check if user exists in database
            User user = userRepository.findUserById(id);
            if (user == null) {
                log.error("User was not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.status(HttpStatus.OK).body(user);
        } catch (Exception e) {
            log.error("Error occurred while finding user by id {}", id);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<Boolean> isUserAdmin(String id) {
        try {
            User user = userRepository.findUserById(id);
            if (user == null) {
                log.error("User was not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.status(HttpStatus.OK).body(user.getIsAdmin());
        } catch (Exception e) {
            log.error("An error occurred while checking if user is admin", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

