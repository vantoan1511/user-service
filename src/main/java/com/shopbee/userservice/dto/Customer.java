package com.shopbee.userservice.dto;

import lombok.*;

import java.sql.Timestamp;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Customer {
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String phone;
    private String address;
    private String address1;
    private String address2;
    private String address3;
    private String address4;
    private String avatarUrl;
    private Gender gender;
    private Timestamp createdAt;
    private Timestamp modifiedAt;
}
