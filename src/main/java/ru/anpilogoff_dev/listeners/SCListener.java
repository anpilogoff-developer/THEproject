package ru.anpilogoff_dev.listeners;

import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;
import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.HibernateValidatorConfiguration;
import ru.anpilogoff_dev.database.dao.RegistrationDAO;
import ru.anpilogoff_dev.database.dao.RegistrationDAOImpl;
import ru.anpilogoff_dev.service.EmailService;
import ru.anpilogoff_dev.service.SignUpService;
import ru.anpilogoff_dev.service.SignUpServiceImpl;

@WebListener
public class SCListener implements ServletContextListener {
    public SCListener() {
    }

    public void contextInitialized(ServletContextEvent sce) {
        DataSource dataSource;
        try {
            Context initialContext = new InitialContext();
            dataSource = (DataSource)initialContext.lookup("java:comp/env/jdbc/THEproject_datasource");
        } catch (NamingException var8) {
            throw new RuntimeException(var8);
        }

        RegistrationDAO userDao = new RegistrationDAOImpl(dataSource);
        SignUpService userService = new SignUpServiceImpl(userDao);
        EmailService emailService = new EmailService();
        ValidatorFactory factory = Validation.byProvider(HibernateValidator.class).configure().buildValidatorFactory();
        sce.getServletContext().setAttribute("userDataService", userService);
        sce.getServletContext().setAttribute("emailService", emailService);
        sce.getServletContext().setAttribute("factory", factory);
    }

    public void contextDestroyed(ServletContextEvent sce) {
        sce.getServletContext().removeAttribute("userDataService");
        ValidatorFactory factory = (ValidatorFactory)sce.getServletContext().getAttribute("factory");
        if (factory != null) {
            factory.close();
        }

    }
}
