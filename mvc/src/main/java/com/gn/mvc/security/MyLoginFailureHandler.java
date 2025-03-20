package com.gn.mvc.security;

import java.io.IOException;
import java.net.URLEncoder;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class MyLoginFailureHandler implements AuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
		
		String errorMessage = "로그인 실패";
		errorMessage = URLEncoder.encode(errorMessage, "UTF-8");
		
		response.sendRedirect("/login?error=true&errorMsg="+errorMessage);
		
	}

}
