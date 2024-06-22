package ru.anpilogoff_dev.controller.auth;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.anpilogoff_dev.database.model.UserModel;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AuthFilterTest {

    @Mock
    private FilterConfig filterConfig;

    @Mock
    private ServletContext servletContext;

    @Mock
    private ValidatorFactory validatorFactory;

    @Mock
    private Validator validator;

    @InjectMocks
    private AuthFilter filter;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @BeforeEach
    void setUp() throws ServletException {
        when(filterConfig.getServletContext()).thenReturn(servletContext);
        when(servletContext.getAttribute("factory")).thenReturn(validatorFactory);
        when(validatorFactory.getValidator()).thenReturn(validator);

        filter.init(filterConfig);
    }

    @Test
    void testDoFilter_ValidCredentials() throws Exception {
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/auth");
        when(request.getParameter("login")).thenReturn("validLogin7");
        when(request.getParameter("password")).thenReturn("ValidPass123");

        // validation success case
        when(validator.validate(any(UserModel.class)))
                .thenReturn(Validation.buildDefaultValidatorFactory().getValidator()
                                .validate(new UserModel("validLogin7", "ValidPass123")));

        filter.doFilter(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterWithInvalidCredentials() throws Exception {
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/auth");
        when(request.getParameter("login")).thenReturn("invalidLogin");
        when(request.getParameter("password")).thenReturn("InvalidPas");

        // validation fail case
        when(validator.validate(any(UserModel.class))).thenReturn(Validation.buildDefaultValidatorFactory().getValidator().validate(new UserModel("invalidLogin", "InvalidPas")));

        StringWriter stringWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));

        filter.doFilter(request, response, filterChain);

        verify(filterChain, never()).doFilter(request, response);

        // response body checking
        JSONObject jsonResponse = new JSONObject(stringWriter.toString());

        assertFalse(jsonResponse.getBoolean("success"));
        assertFalse(jsonResponse.getBoolean("valid"));
        assertFalse(jsonResponse.getJSONArray("errors").isEmpty());
    }
}
