package ru.anpilogoff_dev.controller.auth;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.anpilogoff_dev.database.model.UserModel;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionFilterTest {

    @InjectMocks
    private SessionFilter filter;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain chain;

    @Mock
    private HttpSession session;

    static Stream<Arguments> sessionFilterDataProvider() {
        return Stream.of(
                Arguments.of(true, true, "/auth", true, "/auth"),
                Arguments.of(true, true, "/signup", false, "/home"),
                Arguments.of(false, false, "/auth", false, null),
                Arguments.of(false, false, "/home", false, "/auth")
        );
    }

    @ParameterizedTest
    @MethodSource("sessionFilterDataProvider")
    void testDoFilter(boolean sessionExists,
                      boolean userExist,
                      String uri,
                      boolean invalidateExpected,
                      String redirectURI) throws ServletException, IOException {

        when(request.getRequestURI()).thenReturn(uri);
        when(request.getSession(false)).thenReturn(sessionExists ? session : null);
        lenient().when(session.getAttribute("user")).thenReturn(userExist ? mock(UserModel.class) : null);
        Cookie jsessionIdCookie = new Cookie("JSESSIONID", "value");
        lenient().when(request.getCookies()).thenReturn(new Cookie[]{jsessionIdCookie});

        filter.doFilter(request, response, chain);

        if (invalidateExpected) {
            assertEquals(jsessionIdCookie.getMaxAge(), 0);
            verify(session).invalidate();
        } else {
            verify(session, never()).invalidate();
        }

        if (redirectURI != null) {
            verify(response, times(1)).sendRedirect(redirectURI);
        } else {
            verify(chain, times(1)).doFilter(request, response);
        }
    }

    @Test
    void check() throws ServletException, IOException {
        Cookie jsessionIdCookie = new Cookie("JSESSIONID", "dummyValue");
        when(request.getCookies()).thenReturn(new Cookie[]{jsessionIdCookie});
        when(request.getRequestURI()).thenReturn("/auth");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(mock(UserModel.class));

        filter.doFilter(request, response, chain);

        assertEquals(0, jsessionIdCookie.getMaxAge());
        verify(response).sendRedirect("/auth");
    }

}