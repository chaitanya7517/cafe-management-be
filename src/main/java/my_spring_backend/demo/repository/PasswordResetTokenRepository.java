package my_spring_backend.demo.repository;

import my_spring_backend.demo.model.PasswordResetToken;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PasswordResetTokenRepository extends MongoRepository<PasswordResetToken, String> {
}
