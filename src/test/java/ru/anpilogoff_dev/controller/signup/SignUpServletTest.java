package ru.anpilogoff_dev.controller.signup;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.anpilogoff_dev.database.model.RegistrationStatus;
import ru.anpilogoff_dev.database.model.UserDataObject;
import ru.anpilogoff_dev.database.model.UserModel;
import ru.anpilogoff_dev.service.EmailService;
import ru.anpilogoff_dev.service.SignUpService;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SignUpServletTest {

    @InjectMocks
    SignUpServlet signupServlet;

    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @Mock
    ServletContext servletContext;

    @Mock
    SignUpService signUpService;

    @Mock
    PrintWriter writer1;

    @Mock
    EmailService emailService;

    @Mock
    RequestDispatcher dispatcher;


    @Test
    public void doPostRes() throws IOException {
        when(request.getParameter("login")).thenReturn("login");
        when(request.getParameter("password")).thenReturn("password");
        when(request.getParameter("email")).thenReturn("email");
        when(request.getParameter("nickname")).thenReturn("nickname");
        when(response.getWriter()).thenReturn(writer1);

        UserDataObject userDataObject = new UserDataObject(new UserModel(
                "login",
                "password",
                "email",
                "nickname"
        ), RegistrationStatus.REG_SUCCESS, "UUID-example");


        when(signUpService.registerUser(any(UserDataObject.class))).thenReturn(userDataObject);

        signupServlet.doPost(request, response);

        verify(request, times(1)).getParameter("login");
        verify(request, times(1)).getParameter("password");
        verify(request, times(1)).getParameter("email");
        verify(request, times(1)).getParameter("nickname");

        verify(signUpService).registerUser(any(UserDataObject.class));

        Assertions.assertEquals(userDataObject.getRegistrationStatus(), RegistrationStatus.REG_SUCCESS);
        verify(emailService, times(1)).sendConfirmationEmail(anyString(), any());

        verify(writer1, times(1)).write(anyString());
        verify(writer1, times(1)).flush();

    }

    @Test
    void doGetCheckForwardingOnEmptyQueryString() throws ServletException, IOException {
        when(request.getQueryString()).thenReturn(null);
        when(request.getRequestDispatcher("signup.html")).thenReturn(dispatcher);

        signupServlet.doGet(request, response);

        verify(request, times(1)).getRequestDispatcher("signup.html");
        verify(dispatcher, times(1)).forward(request, response);

    }

    @Test
    void doGetcheckIsRedirectedWhenQueryStringContainsConfirmationParameter() throws ServletException, IOException {
        when(request.getQueryString()).thenReturn("confirmation=UUID-placeholder");
        when(request.getParameter("confirmation")).thenReturn("UUID-placeholder");
        when(signUpService.confirmRegistration(anyString())).thenReturn(true);

        signupServlet.doGet(request, response);

        verify(response, times(1)).sendRedirect("/home");

    }
}