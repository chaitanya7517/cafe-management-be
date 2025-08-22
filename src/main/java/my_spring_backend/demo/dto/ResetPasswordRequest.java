package my_spring_backend.demo.dto;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String newPassword;
}
