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
import javax.servlet.http.HttpFilter;
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
class SignUpFilterTest extends HttpFilter {
    @InjectMocks SignUpFilter filter;

    @Mock HttpServletRequest request;

    @Mock HttpServletResponse response;

    @Mock FilterChain chain;

    @Mock SignUpService service;

    @Mock ServletContext context;



        @Test
        void testRedirect_withNonNullSessionAndAuthorization() throws ServletException, IOException {
            when(request.getServletContext()).thenReturn(context);
//            when(request.getServletContext().getAttribute("userDataService")).thenReturn(service);
            when(request.getSession(false)).thenReturn(mock(HttpSession.class));
            when(request.getHeader("Authorization")).thenReturn("Basic auth_value");

            filter.doFilter(request, response,chain);

            verify(response).sendRedirect(request.getServletContext().getContextPath() + "/home");
        }

    @Test
    void testRequestParamsChecks_withNullSessionAndAuthorization() throws ServletException, IOException {
        when(request.getServletContext()).thenReturn(context);
        when(request.getServletContext().getAttribute("userDataService")).thenReturn(service);
        when(request.getSession(false)).thenReturn(null);
      //  when(request.getHeader("Authorization")).thenReturn(null);

        UserDataObject object = mock(UserDataObject.class);
        Enumeration<String> parameterNames = Collections.enumeration(Arrays.asList("login","password","email","nickname"));

        when(request.getParameterNames()).thenReturn(parameterNames);
        when(request.getParameter(anyString())).thenReturn("anyString");
        when(service.getUser(any(UserModel.class))).thenReturn(object);
        when(response.getWriter()).thenReturn(mock(PrintWriter.class));
        when(object.getConfirmStatus()).thenReturn(ConfirmStatus.CONFIRMED);

        filter.doFilter(request,response,chain);

        verify(request, times(1)).getParameter("login");
        verify(request, times(1)).getParameter("password");
        verify(request, times(1)).getParameter("email");
        verify(request, times(1)).getParameter("nickname");
        verify(response.getWriter(), times(1))
                .write("User with your creds. r already registered and confirmed");
        verify(response.getWriter(),times(1)).flush();
    }

    @Test
    void checkIfFilterChaindDoFilterCallsIfUserNotExists() throws ServletException, IOException {
        when(request.getServletContext()).thenReturn(context);
        when(request.getServletContext().getAttribute("userDataService")).thenReturn(service);
        when(request.getSession(false)).thenReturn(null);

        Enumeration<String> parameterNames = Collections.enumeration(Arrays.asList("login","password","email","nickname"));
        UserModel model = new UserModel("1","2","3","4");
        when(service.getUser(any(UserModel.class))).thenReturn(null);

        when(request.getParameterNames()).thenReturn(parameterNames);
        when(request.getParameter(anyString())).thenReturn("anyString");
        filter.doFilter(request,response,chain);

        verify(chain,times(1)).doFilter(request,response);

    }
}
