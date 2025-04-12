package com.app.merrbioapi.model.dto.request;



import com.app.merrbioapi.model.enums.Gender;
import com.app.merrbioapi.model.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email should not be blank")
    private String email;

    @NotBlank(message = "Password should not be blank")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@#$%^&+=!?_\\-\\[\\]{}()<>,.;:'\\\"\\/\\\\|]).*$",
            message = "Password must contain at least one letter, one digit, and one special character")
    private String password;
    @NotBlank(message = "First name should not be blank")
    private String firstName;

    @NotBlank(message = "Last name should not be blank")
    private String lastName;

    @NotNull(message = "Birth date should not be null")
    @Past(message = "Birth date must be in the past")
    private Date birthDate;

    @NotBlank(message = "Phone number should not be blank")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone number must be valid")
    private String phoneNumber;

    private Role role;

    @NotNull(message = "Gender should not be null")
    private Gender gender;
}
