package ru.anpilogoff_dev.controller.signup;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.anpilogoff_dev.database.model.ConfirmStatus;
import ru.anpilogoff_dev.database.model.UserDataObject;
import ru.anpilogoff_dev.database.model.UserModel;
import ru.anpilogoff_dev.service.SignUpService;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SignUpServletTest {

    @InjectMocks
    SignUpServlet yourServlet;

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

    @BeforeEach
    public void setup() {
     //   MockitoAnnotations.openMocks(this);

        when(request.getParameter("login")).thenReturn("login");
        when(request.getParameter("password")).thenReturn("password");
        when(request.getParameter("email")).thenReturn("email");
        when(request.getParameter("nickname")).thenReturn("nickname");

        when(request.getServletContext()).thenReturn(servletContext);
        when(servletContext.getAttribute("userDataService")).thenReturn(signUpService);
    }

    @Test
    public void doPostRes() throws IOException {
        when(response.getWriter()).thenReturn(writer1);

        UserDataObject userDataObject = new UserDataObject(new UserModel(
                "login",
                "password",
                "email",
                "nickname"
        ), ConfirmStatus.REG_SUCCESS);

       when(signUpService.registerUser(any(UserDataObject.class))).thenReturn(userDataObject);

        yourServlet.doPost(request, response);

        verify(request, times(1)).getParameter("login");
        verify(request, times(1)).getParameter("password");
        verify(request, times(1)).getParameter("email");
        verify(request, times(1)).getParameter("nickname");

        verify(signUpService).registerUser(any(UserDataObject.class));

        Assertions.assertEquals(userDataObject.getRegistrationStatus(),ConfirmStatus.REG_SUCCESS);

        verify(writer1,times(1)).write(anyString());
        verify(writer1,times(1)).flush();

    }
}