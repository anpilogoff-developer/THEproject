package ru.anpilogoff_dev.service;

import jakarta.validation.Valid;
import ru.anpilogoff_dev.database.model.UserDataObject;
import ru.anpilogoff_dev.database.model.UserModel;

public interface SignUpService {
    UserDataObject registerUser(UserDataObject user);
    boolean confirmEmail(UserDataObject object);

    UserDataObject checkIsUserExist(@Valid UserModel user);

}
