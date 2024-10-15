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
public class UserSort {

    @QueryParam("sortBy")
    private UserSortField sortField = UserSortField.DEFAULT;

    @QueryParam("ascending")
    private boolean ascending;
}
