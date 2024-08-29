package com.bit.springboard.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;

@Component
public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String errorMessage = getExceptionMessage(exception);

        // errorMessage to UTF-8 encoding
        errorMessage = URLEncoder.encode(errorMessage, "UTF-8");

        super.setDefaultFailureUrl("/member/login?error=true&errorMsg=" + errorMessage);

        super.onAuthenticationFailure(request, response, exception);
    }

    private String getExceptionMessage(AuthenticationException exception) {
        if(exception instanceof BadCredentialsException) {
            return "WrongPassword";
        }

        if(exception instanceof UsernameNotFoundException || exception.getMessage().contains("member not exist")) {
            return "WrongUsername";
        }

        return "unexpected error";
    }

    }
