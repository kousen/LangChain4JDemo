package com.kousenit.services;

import java.time.LocalDate;

public record Person(
        String firstName,
        String lastName,
        Address address,
        LocalDate birthDate) {

    public record Address(
            String street,
            Integer streetNumber,
            String city) {
    }
}
