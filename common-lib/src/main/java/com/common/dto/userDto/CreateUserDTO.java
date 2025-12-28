package com.common.dto.userDto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateUserDTO {
    @NotBlank(message = "Password can't be empty.")
    @Size(min = 6, message = "Password must be more than 6 characters.")
    private String password;

    @NotBlank(message = "Name can't be empty.")
    @Size(max = 20, min = 2, message = "Name must be more than 2 and less than 20 characters.")
    private String name;

    @NotBlank(message = "Surname can't be empty.")
    @Size(max = 30, min = 2, message = "Surname must more than 2 and be less than 30 characters.")
    private String surname;

    @NotBlank(message = "Email can't be empty.")
    @Email(message = "Email should be valid.")
    @Size(max = 50, min = 7, message = "Email must be more than 7 and less than 50 characters.")
    private String email;

    @NotNull(message = "Birth date can't be null.")
    @Past(message = "Birth date must be in the past.")
    private LocalDate birthDate;

    private boolean active = true;
}
