package ru.anpilogoff_dev.listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.anpilogoff_dev.database.dao.UserDAO;
import ru.anpilogoff_dev.database.dao.UserDAOImpl;
import ru.anpilogoff_dev.service.SignUpService;
import ru.anpilogoff_dev.service.SignUpServiceImpl;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContextEvent;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;

@WebListener
public class SCListener implements javax.servlet.ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        DataSource dataSource;
        Context initialContext;
        try {
            initialContext = new InitialContext();
            dataSource = (DataSource) initialContext.lookup("java:comp/env/jdbc/THEproject_datasource");
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }

       final UserDAO userDao = new UserDAOImpl(dataSource); // Ваша имплементация DAO
       final SignUpService userService = new SignUpServiceImpl(userDao); // Ваша имплементация сервиса
        sce.getServletContext().setAttribute("userDataService", userService);
    }

    @Override
    public void
    contextDestroyed(ServletContextEvent sce) {
        sce.getServletContext().removeAttribute("userDataService");
    }
}
