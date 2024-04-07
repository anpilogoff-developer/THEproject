package ru.anpilogoff_dev.service;


import ru.anpilogoff_dev.database.model.UserDataObject;
import ru.anpilogoff_dev.database.model.UserModel;

public interface SignUpService {
    UserDataObject registerUser(UserDataObject user);
    void updateUser(UserModel user);
    void deleteUser(long userId);

    UserDataObject getUser(UserModel user);

}
