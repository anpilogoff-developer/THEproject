package ru.anpilogoff_dev.database.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class UserDataObject {
    private UserModel userModel;
    private RegistrationStatus registrationStatus;
    private String confirmCode;

    public UserDataObject(UserModel userModel){
        this.userModel = userModel;
    }

    public UserDataObject(RegistrationStatus confirmStatus, String confirmCode){
        this.registrationStatus = confirmStatus;
        this.confirmCode = confirmCode;
    }

    public UserDataObject(UserModel userModel, RegistrationStatus confirmStatus) {
        this.userModel = userModel;
        this.registrationStatus = confirmStatus;
    }

    public String generateConfirmCode(){
        return UUID.randomUUID().toString();
    }
}
