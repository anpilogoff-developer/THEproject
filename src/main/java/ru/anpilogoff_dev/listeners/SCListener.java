package ru.anpilogoff_dev.listeners;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.hibernate.validator.HibernateValidator;
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

       final UserDAO userDao = new UserDAOImpl(dataSource);
       final SignUpService userService = new SignUpServiceImpl(userDao);

        final ValidatorFactory factory = Validation.byProvider(HibernateValidator.class)
                .configure()
                .buildValidatorFactory();
        sce.getServletContext().setAttribute("userDataService", userService);
        sce.getServletContext().setAttribute("factory", factory);


    }

    @Override
    public void
    contextDestroyed(ServletContextEvent sce) {
        sce.getServletContext().removeAttribute("userDataService");
        ValidatorFactory factory = (ValidatorFactory) sce.getServletContext().getAttribute("factory");
        if(factory != null){factory.close();}


    }
}
