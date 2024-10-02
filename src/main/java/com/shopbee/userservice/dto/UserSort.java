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

    @QueryParam("sort_by")
    private UserSortField sortField = UserSortField.DEFAULT;

    @QueryParam("descending")
    private boolean descending;
}
