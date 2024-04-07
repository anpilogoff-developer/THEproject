package ru.anpilogoff_dev.database.dao;

import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.anpilogoff_dev.database.model.ConfirmStatus;
import ru.anpilogoff_dev.database.model.UserDataObject;
import ru.anpilogoff_dev.database.model.UserModel;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAOImpl implements UserDAO {
    private final DataSource dataSource;
    private final String createQuery = "INSERT INTO THEproject_users (login,password,email,nickname,confirmed) VALUES (?,?,?,?,0)";
    private final String getQuery = "SELECT * FROM THEproject_users WHERE login = (?) ";
    private final String setConfirmCodeQuery = "INSERT INTO THEproject_users_confirm_codes (user_login,confirm_code) VALUES (?,?)";

    public UserDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private static final Logger dbLogger = LogManager.getLogger("DatabaseLogger");
    private static final Logger dbErrorLogger = LogManager.getLogger("DatabaseErrorLogger");

    @SneakyThrows
    @Override
    public synchronized UserDataObject create(UserDataObject object) {
        UserModel model = object.getUserModel();
        Connection con = null;
        boolean anyErrors = false;
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement insertStatement = connection.prepareStatement(createQuery);
                PreparedStatement confirmCodeStatement = connection.prepareStatement(setConfirmCodeQuery);
        ) {
            con = connection;
            connection.setAutoCommit(false);

                dbLogger.debug("  --Attempt to insert new user...");

                insertStatement.setString(1,model.getLogin());
                insertStatement.setString(2,model.getPassword());
                insertStatement.setString(3,model.getEmail());
                insertStatement.setString(4,model.getNickname());

                if (insertStatement.executeUpdate() > 0) {

                    dbLogger.debug("  --User insertion status - SUCCESS" + "\n");

                    int confirmCode = object.generateConfirmCode();

                    dbLogger.debug("  --Created user confirmation code generated");

                    confirmCodeStatement.setString(1, object.getUserModel().getLogin());
                    confirmCodeStatement.setInt(2, confirmCode);
                    // код заносится в бд
                    if (confirmCodeStatement.executeUpdate() > 0) {
                        dbLogger.debug("  --Created user confirmation code insertion - successfully");

                        connection.commit();

                        dbLogger.debug("  --Transaction commited... ");

                        object.setConfirmCode(confirmCode);
                        object.setConfirmStatus(ConfirmStatus.REG_SUCCESS);
                    } else anyErrors = true;
                } else {
                    dbLogger.debug("  NOOOOOOOOOOOO...Problem during new user data INSERT method: USER NOT REGISTERED");
                    anyErrors = true;
                }

                if(anyErrors){
                    connection.rollback();
                    object.setConfirmStatus(ConfirmStatus.REG_ERROR);
                }


        } catch (SQLException e) {
            
            dbErrorLogger.warn("SQLException while INSERT QUERY execution:  " + e.getMessage() + "\n" + "  " + e);
            throw new RuntimeException(e);
        }
        dbLogger.debug("  --Connection is closed? : " + con + ":  " + con.isClosed()+ "\n");

        return object;
    }

    @Override
    public  UserDataObject get(UserModel model, Connection con) {
        UserDataObject object = null;
        Connection currentConnection = con;
        if (currentConnection == null) {
            dbLogger.debug("Out of connection. Try establishing connection...");
            try  {
                currentConnection = dataSource.getConnection();
                dbLogger.debug("Connection established:   " + currentConnection);
            } catch (SQLException e) {
                dbErrorLogger.warn("Error during connection establishment: "+ e.getMessage() + "\n" + e);
                throw new RuntimeException(e);
            }
        }

        try (PreparedStatement getStatement = currentConnection.prepareStatement(getQuery)) {
            getStatement.setString(1, model.getLogin());

            try (ResultSet resultSet = getStatement.executeQuery()) {

                dbLogger.debug("User exists checking...");

                if (resultSet.next()) {

                    dbLogger.debug("  --User with login:  " + model.getLogin() + " exists;");

                    model.setPassword(resultSet.getString("password"));
                    model.setEmail(resultSet.getString("email"));
                    model.setNickname(resultSet.getString("nickname"));

                    object = new UserDataObject(model);


                    if (resultSet.getBoolean("confirmed")) {

                        object.setConfirmStatus(ConfirmStatus.CONFIRMED);

                        dbLogger.debug("  --confirmation status: CONFIRMED" + "\n");
                    } else {
                        object.setConfirmStatus(ConfirmStatus.UNCONFIRMED);

                        dbLogger.debug("  --confirmation status: UNCONFIRMED" + "\n");
                    }
                } else dbLogger.debug("  --User with login:  " + model.getLogin() + " NOT exists;");

            }
            if(con == null) {
                currentConnection.close();
                dbLogger.debug(currentConnection.isClosed() + "    <--закрыто ли соедининие");
            }
        } catch (SQLException e) {
            dbErrorLogger.warn("Error during query execution:  " + e);
            throw new RuntimeException(e);
        }
        return object;
    }
}











