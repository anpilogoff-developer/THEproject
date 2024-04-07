package ru.anpilogoff_dev.service;

import ru.anpilogoff_dev.database.dao.UserDAO;
import ru.anpilogoff_dev.database.model.UserDataObject;
import ru.anpilogoff_dev.database.model.UserModel;


public class SignUpServiceImpl implements SignUpService {
    private final UserDAO userDAO;

    public SignUpServiceImpl(UserDAO userDao) {
        this.userDAO = userDao;
    }

    @Override
    public UserDataObject registerUser(UserDataObject object) {return userDAO.create(object);}

    @Override
    public void updateUser(UserModel user) {}

    @Override
    public void deleteUser(long userId) {}

    @Override
    public UserDataObject getUser(UserModel user) {
        return userDAO.get(user, null);
    }

}
