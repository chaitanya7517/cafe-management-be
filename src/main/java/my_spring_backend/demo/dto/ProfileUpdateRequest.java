package my_spring_backend.demo.dto;

import lombok.Data;

@Data
public class ProfileUpdateRequest {
    private String name;
    private String mobileNo;
}