package ru.anpilogoff_dev.database.dao;

import ru.anpilogoff_dev.database.model.UserDataObject;
import ru.anpilogoff_dev.database.model.UserModel;

/**
 * Интерфейс для взаимодействия с базой данных для операций, связанных с регистрацией пользователя.
 * Определяет основные методы для создания нового пользователя, получения информации о пользователе
 * и подтверждения регистрации пользователя по коду.
 */
public interface RegistrationDAO {

    /**
     * Создает новую запись пользователя в базе данных.
     *
     * @param userDataObject Объект данных пользователя, содержащий всю необходимую информацию для регистрации.
     * @return Объект данных пользователя с обновленным статусом регистрации и, возможно, другой дополнительной информацией.
     */
    UserDataObject create(UserDataObject userDataObject);

    UserDataObject get(UserModel user);

    boolean confirm(String code);
}