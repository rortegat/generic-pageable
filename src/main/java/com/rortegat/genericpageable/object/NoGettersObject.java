package com.rortegat.genericpageable.object;

public class NoGettersObject {
    private final String firstName;
    private final String lastName;

    public NoGettersObject(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
