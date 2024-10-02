package com.shopbee.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerUpdate {
    @Length(max = 15, message = "First name exceeds the max length of 15 characters")
    private String firstName;

    @Length(max = 15, message = "Last name exceeds the max length of 15 characters")
    private String lastName;

    @Email(message = "Email must be valid")
    @NotBlank(message = "Email is required")
    private String email;

    @Length(max = 10, min = 10, message = "Phone number length is invalid")
    private String phone;

    private String address;

    private String address1;

    private String address2;

    private String address3;

    private String address4;

    private String avatarUrl;

    private Gender gender;
}
