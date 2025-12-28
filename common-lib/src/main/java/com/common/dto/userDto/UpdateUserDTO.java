package com.common.dto.userDto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateUserDTO {
    @Size(max = 20, min = 2, message = "Name must be more than 2 and less than 20 characters.")
    private String name;

    @Size(max = 30, min = 2, message = "Surname must be more than 2 and less than 30 characters.")
    private String surname;

    @Email(message = "Email should be valid.")
    @Size(max = 50, min = 7, message = "Email must be more than 7 and less than 50 characters.")
    private String email;

    @Past(message = "Birth date must be in the past.")
    private LocalDate birthDate;
}
