package com.gn.mvc.security;

import java.io.IOException;
import java.net.URLEncoder;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class MyLoginFailureHandler implements AuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
		
		/*
			주요 예외:
			BadCredentialsException → 아이디 또는 비밀번호 오류
			UsernameNotFoundException → 존재하지 않는 사용자 -> 이걸 노출해주는건 보안 측면에서 좋지 않을듯
			AccountExpiredException → 계정 만료
			CredentialsExpiredException → 비밀번호 만료
			DisabledException → 계정 비활성화
			LockedException → 계정 잠김
			InsufficientAuthenticationException → 추가 인증 필요
			SessionAuthenticationException → 세션 제한 초과
		*/
		
		String errorMessage = "알 수 없는 이유로 로그인에 실패하였습니다.";
		
		if(exception instanceof BadCredentialsException || exception instanceof InternalAuthenticationServiceException) {
			// 아이디, 비밀번호가 틀렸거나 || 시스템 상의 오류로 아이디, 비밀번호가 제대로 조회되지 않는 경우
			errorMessage = "아이디 또는 비밀번호가 일치하지 않습니다.";
		} else if(exception instanceof DisabledException) {
			// isEnabled() false인 경우 = 계정이 비활성화 된 경우
			errorMessage = "계정이 비활성화 되었습니다.";
		} else if(exception instanceof CredentialsExpiredException) {
			// isCredentialsNonExpired() false 인 경우 = 
			errorMessage = "비밀번호 유효기간이 만료되었습니다.";
		}
		
		
		errorMessage = URLEncoder.encode(errorMessage, "UTF-8");
		
		response.sendRedirect("/login?error=true&errorMsg="+errorMessage);
		
	}

}
