package com.shopbee.userservice.dto;

import com.shopbee.userservice.validator.constraint.UniqueEmail;
import com.shopbee.userservice.validator.constraint.UniqueUsername;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreation {

    @Length(max = 15, message = "First name exceeds the max length of 15 characters")
    private String firstName;

    @Length(max = 15, message = "Last name exceeds the max length of 15 characters")
    private String lastName;

    @Email(message = "Email must be valid")
    @UniqueEmail
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Username is required")
    @Size(max = 25, message = "Username must not exceed 25 characters of length")
    @UniqueUsername
    private String username;

    private String password;

    private boolean temporary;

    private boolean enabled;

    private boolean emailVerified;

}
