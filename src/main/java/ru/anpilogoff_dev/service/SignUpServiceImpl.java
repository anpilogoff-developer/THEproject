package ru.anpilogoff_dev.service;

import ru.anpilogoff_dev.database.dao.RegistrationDAO;
import ru.anpilogoff_dev.database.model.RegistrationStatus;
import ru.anpilogoff_dev.database.model.UserDataObject;
import ru.anpilogoff_dev.database.model.UserModel;

/**
 * Реализация интерфейса RegistrationDAO для работы с базой данных.
 */
public class SignUpServiceImpl implements SignUpService {
   // private static final Logger log = LogManager.getLogger("RuntimeLogger");
    private final RegistrationDAO registrationDAO;

    public SignUpServiceImpl(RegistrationDAO regDAO) {
        this.registrationDAO = regDAO;
    }

    /**
     * Передаёт за объект данных пользователя, включающий в себя модель пользователя, содержащую данные для регистрации
     * и параметр статуса в слой взаимодействия с бд, получает как ответ этот же, но изменённый в зависимости от результата
     * объект, и передаёт его обратно в сервлет.
     *
     * @param object
     * @return Объект данных пользователя содержащий информацию о результате попытки регистрации
     */
    @Override
    public UserDataObject registerUser(UserDataObject object) {
        return registrationDAO.create(object);
    }

    @Override   // указать про хранимую процедуру
    public boolean confirmRegistration(String confirmCode) {
        return registrationDAO.confirm(confirmCode);
    }

    /**
     * Передаёт объект модели пользователя в слой взаимодействия с базой для проверки сущестования пользователя,
     * получает ответ. Если в ответе вернулся  не "null" - извлекает объект модели пользователя(UserModel) из
     * ответа от DAO представленным экземпляром класса UserDataObject.
     *
     * @param user модель пользователя, содержащая регистрационные данные из запроса
     * @return изменённый объект UserDataObject или null.
     */
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
