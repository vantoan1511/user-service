package com.shopbee.userservice.dto;

import jakarta.ws.rs.QueryParam;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserFilter {

    @QueryParam("keyword")
    private String keyword;

    @QueryParam("enabled")
    private Boolean enabled;

    @QueryParam("emailVerified")
    private Boolean emailVerified;
}
