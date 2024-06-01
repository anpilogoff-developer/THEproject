package ru.anpilogoff_dev.database.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.UUID;

/**
 * Объект данных пользователя, содержащий модель пользователя, информацию о статусе регистрации и коде подтверждения.
 *
 * @see UserModel
 * @see RegistrationStatus
 */
@Getter
@Setter
@AllArgsConstructor
@ToString
public class UserDataObject {
    /**
    Используется для передачи регистрационных данных
     */
    private UserModel userModel;

    /**
     * 1)Используется при попытке регистрации для возврата об успешном результате или при возникновении ошибки в процессе.
     * 2)Используется для ответа на запрос о наличии пользователя в базе, а также для указания конкретного параметра
     *  (логин, почта или никнейм) повлиявшего на отрицательный исход процесса регистрации
     */
    private RegistrationStatus registrationStatus;

    /**
     * Используется для сохранения кода подтверждения в таблице users_confirm_codes,
     * передачи его в сервис EmailService для отправки по e-mail,
     * последующее извлечение из URL'а(содержащегося в электронном письме) запроса клиента,
     * передача в сервис регистрации для подтверждения регистрации
     *
     * @see ru.anpilogoff_dev.service.SignUpServiceImpl
     * @see ru.anpilogoff_dev.service.EmailService
     */
    private String confirmCode;

    /**
     * Используется в сервлете регистрации для передачи в сервис регистрационных данных
     *
     * @param userModel Содержит регистрационные данные
     */
    public UserDataObject(UserModel userModel) {
        this.userModel = userModel;
    }

    public UserDataObject(RegistrationStatus confirmStatus, String confirmCode) {
        this.registrationStatus = confirmStatus;
        this.confirmCode = confirmCode;
    }

    public UserDataObject(UserModel userModel, RegistrationStatus confirmStatus) {
        this.userModel = userModel;
        this.registrationStatus = confirmStatus;
    }
    /**
     * Генерирует уникальный код подтверждения для пользователя.
     * @return Строка с уникальным кодом подтверждения.
     */
    public String generateConfirmCode() {
        return UUID.randomUUID().toString();
    }
}
