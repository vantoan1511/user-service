package com.shopbee.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordUpdate {
    @NotBlank(message = "Password is required")
    private String newPassword;
}
