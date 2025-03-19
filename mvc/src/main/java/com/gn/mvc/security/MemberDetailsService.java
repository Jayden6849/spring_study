package com.gn.mvc.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.gn.mvc.entity.Member;
import com.gn.mvc.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberDetailsService implements UserDetailsService {
	
	private final MemberRepository memberRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// username를 기준으로 실제 회원의 정보를 가져오는 로직을 작성 - 해당 정보는 MemberDetails 형태
		// 일치하는 아이디 값과 일치하는 사용자 정보가 있으면, 비밀번호를 암호화하여 일치여부를 판단
		// 불일치할 경우 로그인 페이지에 머물고, 일치할 경우 WebSecurityConfig에 작성해둔 .defaultSuccessUrl("") 로 이동함
		
		Member member = memberRepository.findByMemberId(username);
		
		if(member == null) {
			throw new UsernameNotFoundException("존재하지 않는 회원입니다.");
		}
		
		return new MemberDetails(member);
	}
	
}
