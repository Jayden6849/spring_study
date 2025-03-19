package com.gn.mvc.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.gn.mvc.entity.Member;

import lombok.RequiredArgsConstructor;

// UserDetails(스프링이 사용하는 사용자 정보 객체)를 구현한 구현체
@RequiredArgsConstructor
public class MemberDetails implements UserDetails {
	
	private static final long serialVersionUID = 1L;
	
	// Member 엔티티를 사용할 수 있도록 의존성 주입
	private final Member member;
	
	// Member에 Getter 선언
	public Member getMember() {
		return member;
	}
	
	// 사용자의 권한을 설정해주는 메소드 - 복수형으로 설정 가능 ex) 학생이면서 딸, 인사책임자이면서 남성
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority("user"));
	}
	
	// 사용자 비밀번호를 반환 - member가 가지고 있는 pw정보를 넣어서 쓰겠다
	@Override
	public String getPassword() {
		return member.getMemberPw();
	}
	
	// 사용자 이름을 반환 - member가 가지고 있는 id정보를 넣어서 쓰겠다
	@Override
	public String getUsername() {
		return member.getMemberId();
	}
	
}
