package com.common.dto.userDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDataDTO {
    Integer id;
    String name;
    String surname;
    String email;
    private Boolean active;
}
