package ru.anpilogoff_dev.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.anpilogoff_dev.database.dao.RegistrationDAO;
import ru.anpilogoff_dev.database.model.RegistrationStatus;
import ru.anpilogoff_dev.database.model.UserDataObject;
import ru.anpilogoff_dev.database.model.UserModel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SignUpServiceImplTest {

    @Mock
    RegistrationDAO regDAO;

    @InjectMocks
    SignUpServiceImpl service;

    @Mock
    UserDataObject object;


    @Test
    void registerUser() {
        UserModel userModel = new UserModel("test", "test", "test", "test");
        UserDataObject userDataObject = new UserDataObject(userModel);
        when(regDAO.create(any(UserDataObject.class))).thenReturn(userDataObject);

        UserDataObject result = service.registerUser(userDataObject);

        assertNotNull(result);
        assertEquals(userDataObject, result);
        verify(regDAO).create(userDataObject);
    }

    @Test
    void getUser() {
        UserModel model = new UserModel("test", "test", "test", "test");
        when(regDAO.get(model)).thenReturn(object);
        when(object.getUserModel()).thenReturn(model);
        UserDataObject object = service.checkIsUserExist(model);
        verify(regDAO, times(1)).get(model);
        verify(object, times(1)).getUserModel();
        verify(object, times(1)).setRegistrationStatus(any(RegistrationStatus.class));
        Assertions.assertNotNull(object);
    }

    @Test
    void confirmRegistrationTest() {
        when(regDAO.confirm(anyString())).thenReturn(true);
       boolean confirmed = service.confirmRegistration(anyString());
       Assertions.assertTrue(confirmed);
    }
}