package my_spring_backend.demo.repository;

import my_spring_backend.demo.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
