package ru.anpilogoff_dev.database.model;

import lombok.*;

import java.sql.Date;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class UserModel {
    private long id;
    private String login;
    private String password;
    private String nickname;
    private String email;

    private String registrationDate;
    public UserModel(String login, String password, String nickname, String email) {
        this.login = login;
        this.password = password;
        this.nickname = nickname;
        this.email = email;

    }

    public UserModel(String login) {
        this.login = login;
    }
}
