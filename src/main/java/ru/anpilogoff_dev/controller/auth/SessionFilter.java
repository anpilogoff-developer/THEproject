package ru.anpilogoff_dev.controller.auth;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class SessionFilter implements Filter {

    private static final Logger log = LogManager.getLogger("DebugLogger");

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpSession session = request.getSession(false);
        String uri = request.getRequestURI();


        log.debug("SESSION FILTER:        ");
        log.debug("  + -- requestURI:  "+request.getRequestURI());

        if (session != null && session.getAttribute("user") != null) {

            log.debug("   --- session != null && session.getAttrubute(\"user\") != null:   "+session.getAttribute("user"));

            if (uri.contains("auth")) {
                log.debug("   -- uri contain AUTH");
                Cookie jsessionCookie = getJsessionIdCookie(request.getCookies());

                if(jsessionCookie != null){
                    jsessionCookie.setMaxAge(0);

                    log.debug("   -- :   "+jsessionCookie.getName()+":"+jsessionCookie.getValue());

                    response.addCookie(jsessionCookie);

                    log.debug("   -- cookie-max age setted to 0: "+jsessionCookie.getName()+":"+jsessionCookie.getValue()+" - added to response \n");
                    }
                session.invalidate();

                log.debug(" -- session: "+session.getId()+ "   -- ...invalidated");

                response.sendRedirect("/auth");

                log.debug("   -- redirected from session filter on /auth \n");

                return;
            } else if (uri.contains("signup")) {
                log.debug("   -- redirected from session filter on /home \n");
                response.sendRedirect("/home");
                return;
            }
        }else if (uri.contains("home")){
            response.sendRedirect("/auth");
            return;
        }
        filterChain.doFilter(request, response);
    }

    public Cookie getJsessionIdCookie(Cookie [] cookies){
        log.debug("SessionFilter.getJsessionIdCookie()");
        Cookie jSessionIdCookie = null;

        if (cookies != null && cookies.length != 0) {

            log.debug("   -- cookies != null");

            for (Cookie cookie : cookies) {
                if (cookie != null && cookie.getName().equals("JSESSIONID")) {
                    log.debug( cookie.getName() +"   -- cookies[0].getName()");
                    jSessionIdCookie = cookie;

                    break;
                }
            }
        }
        log.debug("returned "+jSessionIdCookie);

        return jSessionIdCookie;
    }
}
