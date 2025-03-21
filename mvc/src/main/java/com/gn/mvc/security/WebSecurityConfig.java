package com.gn.mvc.security;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import lombok.RequiredArgsConstructor;

@Configuration // 환경설정 파일임을 선언
@EnableWebSecurity // 스프링 시큐리티를 사용하겠다는 선언
@RequiredArgsConstructor
public class WebSecurityConfig {
	
	private final DataSource dataSource;	// application 에 적어놓은 정보로 접근할 수 있는 DB를 알아서 가져옴

	// 요청 중에 정적인 리소스가 있는 경우에는 Security 비활성화 - 스프링시큐리티의 메소드는 @Bean이 붙는다면 모두 다 public 때문에 생략
	@Bean
	WebSecurityCustomizer configure() {
		return web -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
	}
	
	// 특정 요청이 들어왔을 때 어떻게 처리할 것인가? - permitAll()에는 접근할 수 있지만, authenticated()에는 권한이 있어야 접근 가능함.
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http, UserDetailsService customUserDetailsService) throws Exception {
		http.userDetailsService(customUserDetailsService)
		.authorizeHttpRequests(requests -> requests.requestMatchers("/","/login","/signup","/logout").permitAll()
													.requestMatchers("/admin/**").hasRole("ADMIN")
													.anyRequest().authenticated())
		.formLogin(login -> login.loginPage("/login")
								.successHandler(new MyLoginSuccessHandler())
								.failureHandler(new MyLoginFailureHandler()))
		.logout(logout -> logout.logoutUrl("/logout")
								.clearAuthentication(true)
								.logoutSuccessUrl("/login")
								.invalidateHttpSession(true)
								.deleteCookies("remember-me"))
		.rememberMe(rememberMe -> rememberMe.rememberMeParameter("remember-me")
											.tokenValiditySeconds(60*60*24*30)
											.alwaysRemember(false)
											.tokenRepository(tokenRepository()));
		
		return http.build();
	}
	
	// 데이터베이스 접근 Bean 등록 - Spring Securiity는 JDBC 방식을 필요로함
	@Bean
	PersistentTokenRepository tokenRepository() {
		JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
		jdbcTokenRepository.setDataSource(dataSource);
		return jdbcTokenRepository;
	}
	
	// 비밀번호 암호화에 사용될 Bean 등록
	@Bean
	PasswordEncoder passwardEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	// AuthenticationManager(인증 관리)
	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}
	
}
