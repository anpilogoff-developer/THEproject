package ru.anpilogoff_dev.database.dao;

import lombok.SneakyThrows;
import org.apache.logging.log4j.core.util.Assert;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.anpilogoff_dev.database.model.ConfirmStatus;
import ru.anpilogoff_dev.database.model.UserDataObject;
import ru.anpilogoff_dev.database.model.UserModel;

import javax.sql.DataSource;
import java.sql.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDAOImplTest {
    @InjectMocks
    UserDAOImpl dao;
    @Mock
    DataSource dataSource;

    @Mock
    PreparedStatement statementMocked;

    @Mock
    Connection connectionMocked;

    @Mock
    ResultSet resultSetMocked;

    Connection connectionReal;


    String url = "jdbc:mysql://localhost:3306/THEproject"; //это был не копипаст если что.)
    String username = "io";
    String password = "password";

    @BeforeAll
    static void init() throws ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");

    }


    @SneakyThrows
    @AfterEach
    void clean(){
        connectionReal = DriverManager.getConnection(url,username,password);
        PreparedStatement statement = connectionReal.prepareStatement("DELETE FROM users");
        statement.executeUpdate();
        connectionReal.close();
    }



    @Test
    void tryCreateUserWhenNotExists() throws SQLException {

        connectionReal = DriverManager.getConnection(url, username, password);
        when(dataSource.getConnection()).thenReturn(connectionReal);

        UserModel model = new UserModel("test1","test1","test1","test1");
        UserDataObject testUserObject = new UserDataObject(model);
        //Check is registration success
        UserDataObject result = dao.create(testUserObject);

        Assertions.assertEquals(ConfirmStatus.REG_SUCCESS,result.getRegistrationStatus());
        Assertions.assertNotSame(0,result.getConfirmCode());
        Assertions.assertTrue(connectionReal.isClosed());
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
        Assertions.assertEquals(ConfirmStatus.REG_ERROR,result.getRegistrationStatus());
        verify(connectionMocked,times(1)).rollback();

    }

    @SneakyThrows
    @Test
    void checkNullConnectionWillBeEstablishedInGetMethod() {
        connectionReal = DriverManager.getConnection(url,username,password);
        when(dataSource.getConnection()).thenReturn(connectionReal);
        dao.get(new UserModel("test"),null);
        verify(dataSource,times(1)).getConnection();
        Assertions.assertTrue(connectionReal.isClosed());
    }

    @SneakyThrows
    @Test
    void checkIsUserObjectOnNullWhenUserNotExistInGetMethod(){
        UserModel userModel = Mockito.mock(UserModel.class);
        when(connectionMocked.prepareStatement(anyString())).thenReturn(statementMocked);
        when(statementMocked.executeQuery()).thenReturn(resultSetMocked);
        when(resultSetMocked.next()).thenReturn(false);

        UserDataObject result = dao.get(userModel,connectionMocked);

        verify(userModel,times(0)).setPassword(anyString());
        verify(userModel,times(0)).setEmail(anyString());
        verify(userModel,times(0)).setNickname(anyString());
        Assertions.assertNull(result);

    }

    @Test
    @SneakyThrows
    void checkIsUserObjectNotNullWhenUserExistInGetMethod(){

        UserModel userModel = new UserModel("s","null","null","null");
        when(connectionMocked.prepareStatement(anyString())).thenReturn(statementMocked);
        when(statementMocked.executeQuery()).thenReturn(resultSetMocked);
        when(resultSetMocked.next()).thenReturn(true);
        when(resultSetMocked.getString(anyString())).thenReturn("test");

        UserDataObject result = dao.get(userModel,connectionMocked);

        Assertions.assertNotNull(result);
        verify(resultSetMocked,times(1)).next();
        verify(resultSetMocked,times(3)).getString(anyString());
        verify(resultSetMocked,times(1)).getBoolean(any());
        Assertions.assertNotSame(null,result.getUserModel().getPassword());
        Assertions.assertNotSame(null,result.getUserModel().getEmail());
        Assertions.assertNotSame(null,result.getUserModel().getNickname());

        Assertions.assertNotSame(ConfirmStatus.UNKNOWN,result.getRegistrationStatus());
    }

}