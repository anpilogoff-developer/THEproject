package ru.anpilogoff_dev.controller.signup;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.anpilogoff_dev.database.model.ConfirmStatus;
import ru.anpilogoff_dev.database.model.UserDataObject;
import ru.anpilogoff_dev.database.model.UserModel;
import ru.anpilogoff_dev.service.SignUpService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
public class SignUpServlet extends HttpServlet {
    private static final Logger log = LogManager.getLogger("HttpRequestLogger");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {}

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        log.info(":POST:SignupServlet: " + req.getRequestURI()+  "    " );

        UserModel userModel = new UserModel(
                req.getParameter("login"),
                req.getParameter("password"),
                req.getParameter("email"),
                req.getParameter("nickname")
        );

        UserDataObject object = new UserDataObject(userModel,ConfirmStatus.UNKNOWN);
        SignUpService service = (SignUpService) req.getServletContext().getAttribute("userDataService");
        Writer writer = resp.getWriter();


        object = service.registerUser(object);
        switch (object.getConfirmStatus()){
            case REG_SUCCESS:
                log.debug("  --registration success:"+object);
                //TODO EmailService.sendConfirmationEmail(Message message, String email);
                writer.write("User successfully registered,and now needs confirmation by email");
                break;
            case REG_ERROR:
                log.debug("  --registration error:"+object);
                writer.write("Registration error occured, please try again"); break;
        }
        resp.getWriter().flush();
        writer.close();
    }
}
