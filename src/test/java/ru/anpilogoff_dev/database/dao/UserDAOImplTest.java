package ru.anpilogoff_dev.database.dao;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.anpilogoff_dev.database.model.RegistrationStatus;
import ru.anpilogoff_dev.database.model.UserDataObject;
import ru.anpilogoff_dev.database.model.UserModel;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDAOImplTest {
    @InjectMocks
    RegistrationDAOImpl dao;
    @Mock
    DataSource dataSource;

    @Mock
    PreparedStatement statementMocked;

    @Mock
    Connection connectionMocked;

    @Mock
    ResultSet resultSetMocked;


    @Test
    void tryCreateUserWhenNotExists() throws SQLException {

        when(dataSource.getConnection()).thenReturn(connectionMocked);
        when(connectionMocked.prepareStatement(anyString())).thenReturn(statementMocked);
        when(statementMocked.executeUpdate()).thenReturn(1);

        UserModel model = new UserModel("test1","test1","test1","test1");
        UserDataObject testUserObject = new UserDataObject(model);
        UserDataObject result = dao.create(testUserObject);

        Assertions.assertEquals(RegistrationStatus.REG_SUCCESS,result.getRegistrationStatus());
        Assertions.assertNotSame("0",result.getConfirmCode());

    }

    @Test
    void tryCreateUserWhenExists() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connectionMocked);
        when(connectionMocked.prepareStatement(anyString())).thenReturn(statementMocked);
        when(statementMocked.executeUpdate()).thenReturn(0);

        UserModel model = new UserModel("test1","test1","test1","test1");
        UserDataObject testUserObject = new UserDataObject(model);
        //Check is registration not success
        UserDataObject result = dao.create(testUserObject);
        Assertions.assertEquals(RegistrationStatus.REG_ERROR,result.getRegistrationStatus());
        verify(connectionMocked,times(1)).rollback();

    }


    @SneakyThrows
    @Test
    void checkIsUserObjectOnNullWhenUserNotExistInGetMethod(){
        UserModel userModel = Mockito.mock(UserModel.class);
        when(dataSource.getConnection()).thenReturn(connectionMocked);

        when(connectionMocked.prepareStatement(anyString())).thenReturn(statementMocked);
        when(statementMocked.executeQuery()).thenReturn(resultSetMocked);
        when(resultSetMocked.next()).thenReturn(false);

        UserDataObject result = dao.get(userModel);

        verify(userModel,times(0)).setPassword(anyString());
        verify(userModel,times(0)).setEmail(anyString());
        verify(userModel,times(0)).setNickname(anyString());
        Assertions.assertNull(result);

    }

    @Test
    @SneakyThrows
    void checkIsUserObjectNotNullWhenUserExistInGetMethod(){
        when(dataSource.getConnection()).thenReturn(connectionMocked);
        UserModel userModel = new UserModel("s","null","null","null");
        when(connectionMocked.prepareStatement(anyString())).thenReturn(statementMocked);
        when(statementMocked.executeQuery()).thenReturn(resultSetMocked);
        when(resultSetMocked.next()).thenReturn(true);
        when(resultSetMocked.getString(anyString())).thenReturn("test");

        UserDataObject result = dao.get(userModel);

        Assertions.assertNotNull(result);
        verify(resultSetMocked,times(1)).next();
//""""""""""""вернуть        verify(resultSetMocked,times(4)).getString(anyString());
        verify(resultSetMocked,times(1)).getBoolean(any());
        Assertions.assertNotSame(null,result.getUserModel().getPassword());
        Assertions.assertNotSame(null,result.getUserModel().getEmail());
        Assertions.assertNotSame(null,result.getUserModel().getNickname());

        Assertions.assertNotSame(RegistrationStatus.UNKNOWN,result.getRegistrationStatus());
    }

    @SneakyThrows
    @Test
    void checkRollBackOnTryToConfirmInvalidCode(){
        when(dataSource.getConnection()).thenReturn(connectionMocked);
        when(connectionMocked.prepareStatement(anyString())).thenReturn(statementMocked);
        when(statementMocked.executeUpdate()).thenReturn(0);
        boolean result = dao.confirm("invalidCode");
        verify(connectionMocked,times(1)).rollback();
        Assertions.assertFalse(result);
    }

}