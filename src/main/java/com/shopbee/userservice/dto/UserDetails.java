package com.shopbee.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Comparator;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetails implements Comparable<UserDetails> {

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

    private Gender gender;

    private Timestamp createdAt;

    private Timestamp modifiedAt;

    private boolean enabled;

    private boolean emailVerified;

    @Override
    public int compareTo(UserDetails other) {
        if (other == null) return 1;
        return Comparator.nullsFirst(Comparator.<Timestamp>naturalOrder())
                .compare(this.createdAt, other.createdAt);
    }
}
