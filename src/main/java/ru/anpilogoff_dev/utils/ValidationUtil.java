package ru.anpilogoff_dev.utils;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import ru.anpilogoff_dev.database.model.UserModel;

import java.util.Set;

public class ValidationUtil {
    private static final Logger log = LogManager.getLogger("DebugLogger");

    /**
     * Валидирует параметры пользователя, используя Bean Validation API.
     * В случае обнаружения ошибок валидации, создает и возвращает JSON объект с деталями ошибок.
     *
     * @param model    Модель пользователя, содержащая данные для валидации
     * @param validator Валидатор для проверки модели
     * @return JSONObject с результатами валидации или null, если ошибок нет.
     */
    public static JSONObject validateParams(@Valid UserModel model, Validator validator) {
        log.debug(">>>ValidatorUtil.validateParams():");

        Set<ConstraintViolation<UserModel>> violations = validator.validate(model);

        if (!violations.isEmpty()) {
            JSONArray errors = new JSONArray();

            for (ConstraintViolation<UserModel> violation : violations) {
                log.debug("   >>[ERR] validator:  " + violation.getMessage() + "\n");

                JSONObject error = new JSONObject();
                error.put("parameter", violation.getPropertyPath().toString());
                error.put("message", violation.getMessage());
                errors.put(error);
            }

            JSONObject validationError = new JSONObject();
            validationError.put("success", false);
            validationError.put("valid", false);
            validationError.put("errors", errors);

            return validationError;
        } else {
            log.debug("   >>[OK] validator:  validation success");
            return null; // Возвращаем null, если ошибок валидации нет
        }
    }
}
