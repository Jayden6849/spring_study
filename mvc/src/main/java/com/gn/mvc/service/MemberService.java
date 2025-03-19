package com.gn.mvc.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gn.mvc.dto.MemberDto;
import com.gn.mvc.entity.Member;
import com.gn.mvc.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {
	
	private final MemberRepository repository;
	private final PasswordEncoder passwordEncoder;		// 회원가입할 때 암호화해주는 메소드
	
	public MemberDto createMember(MemberDto memberDto) {
		memberDto.setMember_pw(passwordEncoder.encode(memberDto.getMember_pw()));
		
		Member result = repository.save(memberDto.toEntity());
		
		return new MemberDto().toDto(result);
	}
	
}
