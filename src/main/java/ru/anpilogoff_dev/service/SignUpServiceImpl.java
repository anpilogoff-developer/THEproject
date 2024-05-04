package ru.anpilogoff_dev.service;

import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.anpilogoff_dev.database.dao.UserDAO;
import ru.anpilogoff_dev.database.model.ConfirmStatus;
import ru.anpilogoff_dev.database.model.UserDataObject;
import ru.anpilogoff_dev.database.model.UserModel;


public class SignUpServiceImpl implements SignUpService {
    private static final Logger log = LogManager.getLogger();
    private final UserDAO userDAO;

    public SignUpServiceImpl(UserDAO userDao) {
        this.userDAO = userDao;
    }

    @Override
    public UserDataObject registerUser(UserDataObject object) {return userDAO.create(object);}

    @Override
    public boolean confirmEmail(UserDataObject user) {return false;}


    @Override
    public UserDataObject checkIsUserExist(UserModel user) {
        log.debug("SignupService: checkIsUserExist()");
        UserDataObject checked = userDAO.get(user,null);

        if( checked != null && checked.getRegistrationStatus().equals(ConfirmStatus.CONFIRMED)){
            UserModel userModel = checked.getUserModel();

            if(userModel.getLogin().equals(user.getLogin())){
                checked.setRegistrationStatus(ConfirmStatus.CONFIRMED_LOGIN);
            }else if(userModel.getEmail().equals(user.getEmail())){
                checked.setRegistrationStatus(ConfirmStatus.CONFIRMED_EMAIL);
            }else if(userModel.getNickname().equals(user.getNickname())){
                checked.setRegistrationStatus(ConfirmStatus.CONFIRMED_NICKNAME);
            }
        }
        return checked;
    }
}
