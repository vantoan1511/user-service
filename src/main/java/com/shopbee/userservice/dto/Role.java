package com.shopbee.userservice.dto;

import lombok.Getter;

@Getter
public enum Role {
    ADMIN(Constants.ADMIN_VALUE),
    CUSTOMER(Constants.CUSTOMER_VALUE);

    public final String roleName;

    Role(String roleName) {
        this.roleName = roleName;
    }

    public static class Constants {
        
        private Constants() {
        }

        public static final String ADMIN_VALUE = "ADMIN";
        public static final String CUSTOMER_VALUE = "CUSTOMER";

    }

}

