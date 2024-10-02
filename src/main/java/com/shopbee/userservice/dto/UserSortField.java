package com.shopbee.userservice.dto;

import lombok.Getter;

import java.util.Comparator;

@Getter
public enum UserSortField {

    FIRST_NAME(Comparator.comparing(UserDetails::getFirstName, Comparator.nullsFirst(Comparator.naturalOrder()))),

    LAST_NAME(Comparator.comparing(UserDetails::getLastName, Comparator.nullsFirst(Comparator.naturalOrder()))),

    USERNAME(Comparator.comparing(UserDetails::getUsername, Comparator.nullsFirst(Comparator.naturalOrder()))),

    EMAIL(Comparator.comparing(UserDetails::getEmail, Comparator.nullsFirst(Comparator.naturalOrder()))),

    CREATED_AT(Comparator.comparing(UserDetails::getCreatedAt, Comparator.nullsFirst(Comparator.naturalOrder()))),

    ENABLED(Comparator.comparing(UserDetails::isEnabled, Comparator.nullsFirst(Comparator.naturalOrder()))),

    EMAIL_VERIFIED(Comparator.comparing(UserDetails::isEmailVerified, Comparator.nullsFirst(Comparator.naturalOrder()))),

    DEFAULT(Comparator.comparing(UserDetails::getCreatedAt, Comparator.nullsFirst(Comparator.naturalOrder()))
            .thenComparing(UserDetails::getFirstName, Comparator.nullsFirst(Comparator.naturalOrder())));

    private final Comparator<UserDetails> comparator;

    UserSortField(Comparator<UserDetails> comparator) {
        this.comparator = comparator;
    }

}
