package ru.anpilogoff_dev.database.model;

public enum RegistrationStatus {
    UNKNOWN,
    REG_SUCCESS,
    REG_ERROR,
    UNCONFIRMED,
    CONFIRMED,
    LOGIN_EXISTS,
    EMAIL_EXISTS,
    NICKNAME_EXISTS
}
