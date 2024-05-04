package ru.anpilogoff_dev.controller.signup;

import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.AssertFalse;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
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
import javax.servlet.FilterConfig;
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
    ValidatorFactory factory;

    @Mock
    Validator validator;

    @Mock
    PrintWriter writer;

    @Mock
    ServletContext servletContext;



    @Mock
    FilterConfig config;

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
    void checkIsDoFilterCallsOnGetRequest() throws ServletException, IOException {
        when(request.getMethod()).thenReturn("GET");

        filter.doFilter(request,response,chain);

        verify(chain,times(1)).doFilter(request,response);
    }

    @Test
    void testRequestParamsChecks_withNullSessionAndAuthorization() throws ServletException, IOException {
        when(request.getMethod()).thenReturn("POST");
        when(request.getSession(false)).thenReturn(null);
        when(response.getWriter()).thenReturn(writer);
        when(request.getServletContext()).thenReturn(servletContext);
        when(servletContext.getAttribute("userDataService")).thenReturn(service);

        Enumeration<String> parameterNames = Collections.enumeration(Arrays.asList("login", "password", "email", "nickname"));

        when(request.getParameterNames()).thenReturn(parameterNames);
        when(request.getParameter("login")).thenReturn("validLogin7");
        when(request.getParameter("password")).thenReturn("ValidPass123");
        when(request.getParameter("email")).thenReturn("validEmail@test.com");
        when(request.getParameter("nickname")).thenReturn("validNickname");

        UserDataObject object = mock(UserDataObject.class);

        when(service.checkIsUserExist(any(UserModel.class))).thenReturn(object);
        when(object.getRegistrationStatus()).thenReturn(ConfirmStatus.CONFIRMED_LOGIN);

        filter.doFilter(request, response, chain);

        verify(writer).write(contains("reason\":\"login"));
        verify(writer).flush();
    }

    @Test
    void checkIfFilterChainDoFilterCallsIfUserNotExists() throws ServletException, IOException {
        when(request.getMethod()).thenReturn("POST");
        when(request.getSession(false)).thenReturn(null);
        when(request.getServletContext()).thenReturn(servletContext);
        when(servletContext.getAttribute("userDataService")).thenReturn(service);
        when(service.checkIsUserExist(any(UserModel.class))).thenReturn(null);

        Enumeration<String> parameterNames = Collections.enumeration(Arrays.asList("login", "password", "email", "nickname"));

        when(request.getParameterNames()).thenReturn(parameterNames);
        when(request.getParameter("login")).thenReturn("validLogin7");
        when(request.getParameter("password")).thenReturn("ValidPass123");
        when(request.getParameter("email")).thenReturn("validEmail@test.com");
        when(request.getParameter("nickname")).thenReturn("validNickname");

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
    }

    @Test
    void checkValidatorReturnNullOnValidationPass() {
        UserModel model = new UserModel("validLogin7","ValidPass123","validEmail@test.com","validNickname");
        JSONObject res = filter.validateParams(model);

        Assertions.assertNull(res);
    }
}