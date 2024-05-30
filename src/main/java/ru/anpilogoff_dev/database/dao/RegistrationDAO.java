package ru.anpilogoff_dev.database.dao;

import ru.anpilogoff_dev.database.model.UserDataObject;
import ru.anpilogoff_dev.database.model.UserModel;

public interface RegistrationDAO {
    UserDataObject create(UserDataObject userDataObject);

    UserDataObject get(UserModel user);

    boolean confirm(String code);
}