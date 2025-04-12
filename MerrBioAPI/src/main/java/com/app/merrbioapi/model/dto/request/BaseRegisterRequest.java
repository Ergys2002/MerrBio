package com.app.merrbioapi.model.dto.request;

import com.app.merrbioapi.model.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Data; // Using @Data for simplicity, adjust if needed
import java.util.Date;

@Data // Includes @Getter, @Setter, @ToString, @EqualsAndHashCode, @RequiredArgsConstructor
public class BaseRegisterRequest {

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    // Add more password complexity validation if needed (e.g., @Pattern)
    private String password;

    @NotBlank(message = "First name cannot be blank")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    private String lastName;

    @NotNull(message = "Birth date cannot be null")
    @Past(message = "Birth date must be in the past")
    private Date birthDate;

    @NotBlank(message = "Phone number cannot be blank")
    // Add specific phone number format validation if needed (e.g., @Pattern)
    private String phoneNumber;

    @NotNull(message = "Gender cannot be null")
    private Gender gender;
}