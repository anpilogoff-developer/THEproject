package ru.anpilogoff_dev.database.dao;

import ru.anpilogoff_dev.database.model.UserDataObject;
import ru.anpilogoff_dev.database.model.UserModel;

import java.sql.Connection;

public interface UserDAO {
    UserDataObject create(UserDataObject model);
    UserDataObject get(UserModel model, Connection connection);
}
