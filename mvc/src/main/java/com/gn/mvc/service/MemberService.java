package com.gn.mvc.service;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
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
	private final DataSource dataSource;
	private final UserDetailsService userDetailsService;
	
	public MemberDto createMember(MemberDto memberDto) {
		memberDto.setMember_pw(passwordEncoder.encode(memberDto.getMember_pw()));
		
		Member result = repository.save(memberDto.toEntity());
		
		return new MemberDto().toDto(result);
	}
	
	public Member selectMemberOne(Long memberNo) {
		Member result = repository.findById(memberNo).orElse(null);
		
		return result;
	}
	
	public int updateMember(MemberDto memberDto) {
		
		int result = 0;
		
		try {
			// 1. 데이터베이스 회원 정보 수정
			memberDto.setMember_pw(passwordEncoder.encode(memberDto.getMember_pw()));
			
			Member updated = repository.save(memberDto.toEntity());
			
			if(updated != null) {	
				// 2. remember-me(** DB **, Cookie)가 있다면 무효화 - DB 는 서비스에서, Cookie는 컨트롤러에서 다루는 것이 적절함 
				JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
				String sql = "DELETE FROM `persistent_logins` WHERE `username` = ?";
				jdbcTemplate.update(sql, memberDto.getMember_id());
				
				// 3. 변경된 회원 정보 Security Context 에 즉시 반영 - session 수정해 주는 로직
				UserDetails updatedUserDetails = userDetailsService.loadUserByUsername(memberDto.getMember_id());
				Authentication newAuth = new UsernamePasswordAuthenticationToken(updatedUserDetails, updatedUserDetails.getPassword(), updatedUserDetails.getAuthorities());
				SecurityContextHolder.getContext().setAuthentication(newAuth);
				
				result = 1;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public int deleteMember(Long memberNo) {
		int result = 0;
		
		try {
			Member target = repository.findById(memberNo).orElse(null);
			MemberDto memberDto = new MemberDto().toDto(target);
			
			if(target != null) {
				
				repository.delete(target);
				
				JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
				String sql = "DELETE FROM `persistent_logins` WHERE `username` = ?";
				jdbcTemplate.update(sql, memberDto.getMember_id());
				
				SecurityContextHolder.getContext().setAuthentication(null);		// session 만료시키는 코드

				result = 1;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
}
