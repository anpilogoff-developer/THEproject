package ru.anpilogoff_dev.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.anpilogoff_dev.database.dao.UserDAO;
import ru.anpilogoff_dev.database.model.RegistrationStatus;
import ru.anpilogoff_dev.database.model.UserDataObject;
import ru.anpilogoff_dev.database.model.UserModel;


public class SignUpServiceImpl implements SignUpService {
    private static final Logger log = LogManager.getLogger("RuntimeLogger");
    private final UserDAO userDAO;

    public SignUpServiceImpl(UserDAO userDao) {
        this.userDAO = userDao;
    }

    @Override
    public UserDataObject registerUser(UserDataObject object) {return userDAO.create(object);}

    @Override   // указать про хранимую процедуру
    public boolean confirmRegistration(String confirmCode) {return userDAO.confirm(confirmCode);}


    @Override
    public UserDataObject checkIsUserExist(UserModel user) {
        log.debug("SignupService: checkIsUserExist()");

        UserDataObject checked = userDAO.get(user);

        if(checked != null) {
            UserModel userModel = checked.getUserModel();
            if (checked.hashCode() != user.hashCode()) {
                if (userModel.getLogin().equals(user.getLogin())) {
                    checked.setRegistrationStatus(RegistrationStatus.LOGIN_EXISTS);
                } else if (userModel.getEmail().equals(user.getEmail())) {
                    checked.setRegistrationStatus(RegistrationStatus.EMAIL_EXISTS);
                } else if (userModel.getNickname().equals(user.getNickname())) {
                    checked.setRegistrationStatus(RegistrationStatus.NICKNAME_EXISTS);
                }
            }
        }
        return checked;
    }
}
