package org.example.user.Repositories;

import org.example.clients.Entities.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    User findUserByEmail(String email);
    User findUserById(String id);
}
