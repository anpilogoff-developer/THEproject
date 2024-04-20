package ru.anpilogoff_dev.controller.signup;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.anpilogoff_dev.database.model.ConfirmStatus;
import ru.anpilogoff_dev.database.model.UserDataObject;
import ru.anpilogoff_dev.database.model.UserModel;
import ru.anpilogoff_dev.service.SignUpService;
import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SignUpFilterTest {
    @InjectMocks
    SignUpFilter filter;

    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @Mock
    FilterChain chain;

    @Mock
    SignUpService service;

    @Mock
    PrintWriter writer;

    @Mock
    ServletContext servletContext;

    @Test
    void testRedirect_withNonNullSessionAndAuthorization() throws ServletException, IOException {
        when(request.getSession(false)).thenReturn(mock(HttpSession.class));
        when(request.getHeader("Authorization")).thenReturn("Basic auth_value");
        when(request.getServletContext()).thenReturn(servletContext);
        when(servletContext.getContextPath()).thenReturn("/THEproject-backend-fork");

        filter.doFilter(request, response, chain);

        verify(response).sendRedirect("/THEproject-backend-fork/home");
    }

    @Test
    void testRequestParamsChecks_withNullSessionAndAuthorization() throws ServletException, IOException {
        when(request.getSession(false)).thenReturn(null);
//        when(request.getHeader("Authorization")).thenReturn(null);
        when(response.getWriter()).thenReturn(writer);
        when(request.getServletContext()).thenReturn(servletContext);
        when(servletContext.getAttribute("userDataService")).thenReturn(service);

        UserDataObject object = mock(UserDataObject.class);
        when(service.checkIsUserExist(any(UserModel.class))).thenReturn(object);
        when(object.getConfirmStatus()).thenReturn(ConfirmStatus.CONFIRMED_LOGIN);

        Enumeration<String> parameterNames = Collections.enumeration(Arrays.asList("login", "password", "email", "nickname"));
        when(request.getParameterNames()).thenReturn(parameterNames);
        when(request.getParameter(anyString())).thenReturn("validInput");

        filter.doFilter(request, response, chain);

        verify(writer).write(contains("User with your login. r already registered..."));
        verify(writer).flush();
    }

    @Test
    void checkIfFilterChainDoFilterCallsIfUserNotExists() throws ServletException, IOException {
        when(request.getSession(false)).thenReturn(null);
//        when(request.getHeader("Authorization")).thenReturn(null);
        when(request.getServletContext()).thenReturn(servletContext);
        when(servletContext.getAttribute("userDataService")).thenReturn(service);

        when(service.checkIsUserExist(any(UserModel.class))).thenReturn(null);


        Enumeration<String> parameterNames = Collections.enumeration(Arrays.asList("login", "password", "email", "nickname"));
        when(request.getParameterNames()).thenReturn(parameterNames);
        when(request.getParameter(anyString())).thenReturn("validInput");

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
    }
}