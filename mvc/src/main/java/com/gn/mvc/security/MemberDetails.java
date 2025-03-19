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
	
	// 아래 3개의 메소드는 반드시 구현해줘야함
	
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
	
	// 여기까지가 필요 요소
	
	// ==============================================================================================================
	
	// 계정 상태를 관리해주는 메소드 - 옵션
	// is~ 로 시작하며, 반환타입이 boolean
	
	// 계정 만료 여부를 반환하는 메소드 - ex. 임시계정, 비활성계정 - 컬럼에 비활성여부를 담는 컬럼 하나 작성해서 쓰면 됨 - (퇴사여부)
	@Override
	public boolean isAccountNonExpired() {
//		if(member.getExpired().equals("Y") {
//			return false;
//		} else {
//			return true;
//		}
				
		return true;	// true여야 로그인이 되고, false면 로그인이 안 됌 - 지금은 멤버테이블에 퇴사여부가 없어서 그냥 true 하드코딩 
	}
	
	// 계정 잠금 여부를 반환하는 메소드 - ex. 비밀번호가 5번 틀리면 -> 10분간 로그인 금지 등 - 계정만료 컬럼을 하나 만들어서 작성하면 됨
	@Override
	public boolean isAccountNonLocked() {
//		if(member.getLockedDate() + 10분 > 현재시간) {
//			return false;
//		} else {
//			return true;
//		}

		return true;	// true여야 로그인이 되고, false면 로그인이 안 됌
	}
	
	// 패스워드 만료 여부를 반환하는 메소드 - ex. 6개월이 지났음 -> 비밀번호 변경 요청
	@Override
	public boolean isCredentialsNonExpired() {
		return true;	// true여야 로그인이 되고, false면 로그인이 안 됌
	}
	
	// 계정 사용 가능 여부 - 기타 다른 이유들을 포괄
	@Override
	public boolean isEnabled() {
		return true;	// true여야 로그인이 되고, false면 로그인이 안 됌
	}
	
}
