package ru.anpilogoff_dev.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.anpilogoff_dev.database.dao.UserDAO;
import ru.anpilogoff_dev.database.model.RegistrationStatus;
import ru.anpilogoff_dev.database.model.UserDataObject;
import ru.anpilogoff_dev.database.model.UserModel;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SignUpServiceImplTest {

    @Mock
    UserDAO dao;

    @InjectMocks
    SignUpServiceImpl service;

    @Mock
    UserDataObject object;



    @Test
    void registerUser() {
        UserModel userModel = new UserModel("test", "test", "test", "test");
        UserDataObject userDataObject = new UserDataObject(userModel);
        when(dao.create(any(UserDataObject.class))).thenReturn(userDataObject);

        UserDataObject result = service.registerUser(userDataObject);

        assertNotNull(result);
        assertEquals(userDataObject, result);
        verify(dao).create(userDataObject);
    }

    @Test
    void getUser() {
       UserModel model = new UserModel("test","test","test","test");
        when(dao.get(model)).thenReturn(object);
        when(object.getUserModel()).thenReturn(model);
        when(object.getRegistrationStatus()).thenReturn(RegistrationStatus.CONFIRMED);

        UserDataObject object = service.checkIsUserExist(model);
        verify(dao, times(1)).get(model);
        verify(object,times(1)).getRegistrationStatus();
        verify(object,times(1)).getUserModel();
        verify(object,times(1)).setRegistrationStatus(any(RegistrationStatus.class));
        Assertions.assertNotNull(object);
    }

    @Test
    void confirmRegistrationTest(){

    }
}