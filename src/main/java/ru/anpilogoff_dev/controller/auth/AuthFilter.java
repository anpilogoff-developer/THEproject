package ru.anpilogoff_dev.controller.auth;

import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import ru.anpilogoff_dev.database.model.UserModel;
import ru.anpilogoff_dev.utils.ValidationUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;

public class AuthFilter implements Filter {
    private static final Logger log = LogManager.getLogger("RuntimeLogger");
    private Validator validator;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //  Filter.super.init(filterConfig);
        ServletContext context = filterConfig.getServletContext();
        ValidatorFactory validatorFactory = (ValidatorFactory) context.getAttribute("factory");
        this.validator = validatorFactory.getValidator();
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        log.debug("AuthFilter.doFilter():   ");

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String method = request.getMethod();
        String uri = request.getRequestURI();

        log.debug("   -- session exists?: " + request.getSession(false));

        if (uri.contains("auth")) {
            if (method.equals("POST")) {
                log.debug("   -- /auth:\"POST\" \n");

                String login = request.getParameter("login");
                String password = request.getParameter("password");
                JSONObject validationErrors = null;

                if (login != null && password != null) {
                    log.debug("   -- login & pwd not null");

                    validationErrors = ValidationUtil.validateParams(new UserModel(login, password), validator);
                }

                if (validationErrors != null) {
                    log.debug("   -- validation errors(writing to response...");

                    try (Writer writer = response.getWriter()) {
                        writer.write(validationErrors.toString());
                        writer.flush();
                    } catch (IOException e) {
                        log.debug("   -- [EXCEPTION]:Exception during response writing: " + e.getMessage());
                    }
                    log.debug("   -- return  \n");

                    return;
                } else {
                    log.debug("   -- validation success(return null");
                }
            } else if (method.equals("GET")) {
                log.debug("   -- /auth:\"GET\"");
            }
        }
        log.debug("   -- filterchain.doFilter() \n");
        filterChain.doFilter(request, response);
    }
}

