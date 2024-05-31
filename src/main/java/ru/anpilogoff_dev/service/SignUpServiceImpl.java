package ru.anpilogoff_dev.service;

import ru.anpilogoff_dev.database.dao.RegistrationDAO;
import ru.anpilogoff_dev.database.model.RegistrationStatus;
import ru.anpilogoff_dev.database.model.UserDataObject;
import ru.anpilogoff_dev.database.model.UserModel;

public class SignUpServiceImpl implements SignUpService {
   // private static final Logger log = LogManager.getLogger("RuntimeLogger");
    private final RegistrationDAO registrationDAO;

    public SignUpServiceImpl(RegistrationDAO regDAO) {
        this.registrationDAO = regDAO;
    }

    @Override
    public UserDataObject registerUser(UserDataObject object) {
        return registrationDAO.create(object);
    }

    @Override   // указать про хранимую процедуру
    public boolean confirmRegistration(String confirmCode) {
        return registrationDAO.confirm(confirmCode);
    }

    @Override
    public UserDataObject checkIsUserExist(UserModel user) {
        UserDataObject checked = registrationDAO.get(user);

        if (checked != null) {
            UserModel userModel = checked.getUserModel();
            if (userModel.getLogin().equals(user.getLogin())) {
                checked.setRegistrationStatus(RegistrationStatus.LOGIN_EXISTS);
            } else if (userModel.getEmail().equals(user.getEmail())) {
                checked.setRegistrationStatus(RegistrationStatus.EMAIL_EXISTS);
            } else if (userModel.getNickname().equals(user.getNickname())) {
                checked.setRegistrationStatus(RegistrationStatus.NICKNAME_EXISTS);
            }
        }
        return checked;
    }
}
