package ru.anpilogoff_dev.database.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class UserDataObject {
    private UserModel userModel;
    private ConfirmStatus confirmStatus;
    private int confirmCode;

    public UserDataObject(UserModel userModel){
        this.userModel = userModel;
    }

    public UserDataObject(ConfirmStatus confirmStatus, int confirmCode){
        this.confirmStatus = confirmStatus;
        this.confirmCode = confirmCode;
    }

    public UserDataObject(UserModel userModel, ConfirmStatus confirmStatus) {
        this.userModel = userModel;
        this.confirmStatus = confirmStatus;
    }

    public int generateConfirmCode(){
        return this.userModel.hashCode();
    }
}
