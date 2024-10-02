package com.shopbee.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class UserUpdate {

    @Length(max = 15, message = "First name exceeds the max length of 15 characters")
    private String firstName;

    @Length(max = 15, message = "Last name exceeds the max length of 15 characters")
    private String lastName;

    @Email(message = "Email must be valid")
    @NotBlank(message = "Email is required")
    private String email;

    private boolean enabled;

    private boolean emailVerified;

}
