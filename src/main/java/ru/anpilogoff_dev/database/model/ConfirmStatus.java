package ru.anpilogoff_dev.database.model;

public enum ConfirmStatus {
    UNKNOWN,
    REG_SUCCESS,
    REG_ERROR,
    UNCONFIRMED,
    CONFIRMED,
    CONFIRMED_LOGIN,
    CONFIRMED_EMAIL,
    CONFIRMED_NICKNAME
}
