package ru.anpilogoff_dev.utils;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.anpilogoff_dev.database.model.UserModel;



@ExtendWith(MockitoExtension.class)
public class ValidationUtilTest {
    private static final Logger log = LogManager.getLogger("DebugLogger");

    private ValidatorFactory factory;

    @BeforeEach
    void setUp(){
       factory = Validation.buildDefaultValidatorFactory();
       log.debug("factory initialized");

    }

    @AfterEach
    void destroy(){
        if (factory != null){
            factory.close();
            log.debug("factory destroyed");

        }
    }
    @Test
    void testIfValidUserDataValidationReturnNull() {
        Validator validator = factory.getValidator();

        JSONObject object = ValidationUtil.validateParams(new UserModel("vaLidLogin777", "ValidPass123"), validator);
        Assertions.assertNull(object); // Ожидаем, что object будет null, так как данные модели валидны
        factory.close();

    }

    @Test
    void testInvalidUserDataValidationReturnNotNull() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        UserModel model = new UserModel("1", "1");
        JSONObject object = ValidationUtil.validateParams(model, validator);
        Assertions.assertNotNull(object); // Ожидаем, что object не null, так как данные модели невалидны
        factory.close();
    }
}
