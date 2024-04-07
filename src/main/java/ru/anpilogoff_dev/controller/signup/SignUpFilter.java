package ru.anpilogoff_dev.controller.signup;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.anpilogoff_dev.database.model.UserDataObject;
import ru.anpilogoff_dev.database.model.UserModel;
import ru.anpilogoff_dev.service.SignUpService;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SignUpFilter implements Filter {
    private static final Logger log = LogManager.getLogger("HttpRequestLogger");

    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        Writer writer = resp.getWriter();

        if (req.getSession(false) != null && req.getHeader("Authorization") != null) {
            resp.sendRedirect(req.getServletContext().getContextPath() + "/home");
        } else {
            SignUpService service = (SignUpService) req.getServletContext().getAttribute("userDataService");

            List<String> params = new ArrayList<>();
            boolean allParamsValid = Collections.list(req.getParameterNames())
                    .stream()
                    .allMatch(param -> {
                        String paramValue = req.getParameter(param);
                        if (paramValue != null && !paramValue.isEmpty()) {
                            params.add(paramValue);
                            return true;
                        }
                        return false;
                    });
            if (allParamsValid) {
                UserDataObject object = service.getUser(
                        new UserModel(params.get(0), params.get(1), params.get(2), params.get(3)));

                if (object == null) {
                    log.info("SignupFilter: NOT EXISTS");
                    filterChain.doFilter(servletRequest, servletResponse);
                } else {
                    switch (object.getConfirmStatus()) {
                        case CONFIRMED:
                            log.debug("SignupLogger: {registered:confirmed");
                            writer.write("User with your creds. r already registered and confirmed");
                            break;
                        case UNCONFIRMED:
                            log.debug("SignupLogger: {registered:unconfirmed");
                            writer.write("User with your creds. r already registered but needs confirmation");
                            break;
                    }
                    writer.flush();
                }
            }
        }
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
