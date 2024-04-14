package org.example.clients.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.clients.Entities.User;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginUserResponse {
    private String message;
    private User user;
    private String token;
}
