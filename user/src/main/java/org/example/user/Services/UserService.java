package org.example.user.Services;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.user.Entities.User;
import org.example.user.Repositories.UserRepository;
import org.example.user.dto.RegisterUserResponse;
import org.example.user.utilities.JwtTokenUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@AllArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterUserResponse registerUser(String username, String email, String password) {
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
        User newUser = User.builder()
                .username(username)
                .email(email)
                .password(encryptedPassword)
                .isAdmin(false)
                .bookmarkedArticles(new ArrayList<>())
                .build();

        //Save new user to database
        userRepository.save(newUser);

        // Generate JWT token
        String token = JwtTokenUtil.generateToken(newUser.getId());

        log.info("User created successfully {}", email);
        return RegisterUserResponse.builder()
                .message("User created successfully")
                .user(newUser)
                .token(token)
                .build();
    }
}
