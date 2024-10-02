package com.shopbee.userservice.dto;

import com.shopbee.userservice.validator.constraint.UniqueEmail;
import com.shopbee.userservice.validator.constraint.UniqueUsername;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerRegistration {
    @Length(max = 15, message = "First name exceeds the max length of 15 characters")
    @NotBlank(message = "First name is required")
    private String firstName;

    @Length(max = 15, message = "Last name exceeds the max length of 15 characters")
    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Username is required")
    @Size(max = 25, message = "Username must not exceed 25 characters of length")
    @UniqueUsername
    private String username;

    @Email(message = "Email is invalid")
    @NotBlank(message = "Email is required")
    @UniqueEmail
    private String email;

    @NotBlank(message = "Password is required")
    private String password;
}
