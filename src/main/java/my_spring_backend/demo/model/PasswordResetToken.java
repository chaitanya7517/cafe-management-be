package my_spring_backend.demo.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Document(collection = "password_reset_tokens")
public class PasswordResetToken {
    @Id
    private String id;

    private String email;

    // MongoDB TTL index → deletes doc 120s after createdAt, try to use minimum of 120 sec
    @Indexed(expireAfterSeconds = 120)
    private Instant createdAt = Instant.now();
}


