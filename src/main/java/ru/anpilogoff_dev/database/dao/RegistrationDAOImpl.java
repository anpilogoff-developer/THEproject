package ru.anpilogoff_dev.database.dao;

import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.anpilogoff_dev.database.model.RegistrationStatus;
import ru.anpilogoff_dev.database.model.UserDataObject;
import ru.anpilogoff_dev.database.model.UserModel;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RegistrationDAOImpl implements RegistrationDAO {
    private final DataSource dataSource;

    public RegistrationDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private static final Logger dbLogger = LogManager.getLogger("DatabaseLogger");
    private static final Logger dbErrorLogger = LogManager.getLogger("DatabaseErrorLogger");
    private static final Logger log = LogManager.getLogger("RuntimeLogger");




    @SneakyThrows
    @Override
    public synchronized UserDataObject create(UserDataObject object) {
        dbLogger.debug("signupDAO: create()");
        String createQuery = "INSERT INTO users (login,password,email,nickname,confirmed) VALUES (?,?,?,?,0)";
        String setConfirmCodeQuery = "INSERT INTO users_confirm_codes (user_login,confirm_code) VALUES (?,?)";

        UserModel model = object.getUserModel();
        Connection con;
        boolean anyErrors = false;
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement insertStatement = connection.prepareStatement(createQuery);
                PreparedStatement confirmCodeStatement = connection.prepareStatement(setConfirmCodeQuery);
        ) {
            con = connection;
            connection.setAutoCommit(false);

                dbLogger.debug("  --Trying to insert new user...");

                insertStatement.setString(1,model.getLogin());
                insertStatement.setString(2,model.getPassword());
                insertStatement.setString(3,model.getEmail());
                insertStatement.setString(4,model.getNickname());

                if (insertStatement.executeUpdate() > 0) {
                    dbLogger.debug("  --User insertion status - SUCCESS" + "\n");

                    String confirmCode = object.generateConfirmCode();

                    dbLogger.debug("  --Created user confirmation code generated");

                    confirmCodeStatement.setString(1, object.getUserModel().getLogin());
                    confirmCodeStatement.setString(2, confirmCode);

                    if (confirmCodeStatement.executeUpdate() > 0) {
                        dbLogger.debug("  --Created user confirmation code insertion - successfully");

                        connection.commit();

                        dbLogger.debug("  --Transaction commited... ");

                        object.setConfirmCode(confirmCode);
                        object.setRegistrationStatus(RegistrationStatus.REG_SUCCESS);
                    } else anyErrors = true;
                } else {
                    anyErrors = true;
                }
                if(anyErrors){
                    connection.rollback();
                    connection.setAutoCommit(true);
                    object.setRegistrationStatus(RegistrationStatus.REG_ERROR);
                    dbLogger.debug("    ---Problem during new user data INSERT method: USER NOT REGISTERED");
                }
        } catch (SQLException e) {
            dbErrorLogger.warn("SQLException while INSERT QUERY execution:  " + e.getMessage() + "\n" + "  " + e);
            throw new RuntimeException(e);
        }
        dbLogger.debug("  --Connection is closed? : " + con + ":  " + con.isClosed()+ "\n");
        return object;
    }

    @Override
    public synchronized UserDataObject get(UserModel model) {
        log.debug("UserDAOImpl: get(model)... ");
        UserDataObject object = null;

        String getQuery = "SELECT * FROM users WHERE login = ? OR email = ? OR nickname = ? ";
        try(
            Connection connection = dataSource.getConnection();
            PreparedStatement getStatement = connection.prepareStatement(getQuery)) {
            getStatement.setString(1, model.getLogin());
            getStatement.setString(2,model.getEmail());
            getStatement.setString(3,model.getNickname());
            try (ResultSet resultSet = getStatement.executeQuery()) {
                if (resultSet.next()) {
                    UserModel user = new UserModel();

                    user.setLogin(resultSet.getString("login"));
                    user.setPassword(resultSet.getString("password"));
                    user.setEmail(resultSet.getString("email"));
                    user.setNickname(resultSet.getString("nickname"));

                    object = new UserDataObject(user);

                    if (resultSet.getBoolean("confirmed")) {
                        object.setRegistrationStatus(RegistrationStatus.CONFIRMED);
                        log.debug("  --confirmation status: CONFIRMED");
                    } else {
                        object.setRegistrationStatus(RegistrationStatus.UNCONFIRMED);
                        dbLogger.debug("  --confirmation status: UNCONFIRMED");
                    }
                } else dbLogger.debug("  --User NOT exists;");
            }catch (RuntimeException e){
                log.debug("ошибка при выполнении getQuery(SQL)" + e.getMessage());
            }
        } catch (SQLException e) {
            dbErrorLogger.warn("    ---Error during connection establishment: \n" + e.getMessage());
            throw new RuntimeException(e);
        }
        return object;
    }


    //WARNING:
    @Override
    public synchronized boolean confirm(String confirmCode) {
        dbLogger.debug("signupDAO: confirmUser()");
        boolean isConfirmed = false;
        String deleteConfirmCodeQuery = "DELETE FROM users_confirm_codes WHERE confirm_code = (?)";
        try(
            Connection connection = dataSource.getConnection();
            PreparedStatement deleteStatement = connection.prepareStatement(deleteConfirmCodeQuery)
        ){
            connection.setAutoCommit(false);
            deleteStatement.setString(1,confirmCode);
            int affectedRows = deleteStatement.executeUpdate();
            if(affectedRows > 0){
                connection.commit();
                dbLogger.debug("  --User confirmation code deletion - SUCCESS");
                isConfirmed = true;
            }else {
                dbLogger.debug("  --User confirmation code deletion - FAILED");
                connection.rollback();
            }
        }catch (SQLException e){
            dbErrorLogger.warn("SQLException while DELETE QUERY execution:  " + e.getMessage());
            throw new RuntimeException(e);
        }
        return isConfirmed;
    }
}











