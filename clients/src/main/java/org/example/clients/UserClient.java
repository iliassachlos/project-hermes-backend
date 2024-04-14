package org.example.clients;

import org.example.clients.Entities.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("user")
public interface UserClient {

    @GetMapping("api/users/{id}")
    User getUserById(@PathVariable String id);
}
