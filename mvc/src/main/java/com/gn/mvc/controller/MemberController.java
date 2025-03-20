package com.gn.mvc.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gn.mvc.dto.MemberDto;
import com.gn.mvc.entity.Member;
import com.gn.mvc.service.MemberService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MemberController {
	
	private final MemberService service;
	
	@GetMapping("/login")
	public String loginView(
			@RequestParam(value="error", required=false) String error,			// 있으면 매개받고 없으면 매개받지 않게 만든 코드
			@RequestParam(value="errorMsg", required=false) String errorMsg
			,Model model) {
		
		model.addAttribute("error", error);
		model.addAttribute("errorMsg", errorMsg);
		
		return "member/login";
	}
	
	@PostMapping("/signup")
	@ResponseBody
	public Map<String, String> createMemberApi(MemberDto memberDto) {
		Map<String, String> resultMap = new HashMap<>();
		
		resultMap.put("res_code", "500");
		resultMap.put("res_msg", "회원가입 중 오류가 발생하였습니다.");
		
		MemberDto resultDto = service.createMember(memberDto);
		
		if(resultDto != null) {
			resultMap.put("res_code", "200");
			resultMap.put("res_msg", "회원가입이 완료되었습니다.");
		}
		
		return resultMap;
	}
	
	@GetMapping("/signup")
	public String createMemberView() {
		return "member/create";
	}
	
	@GetMapping("/member/{id}/update")
	public String updateMemberView(@PathVariable("id") Long memberNo, Model model) {
		Member member = service.selectMemberOne(memberNo);
		model.addAttribute("member", member);
		return "member/update";
	}
	
	@PostMapping("/member/{id}/update")
	@ResponseBody
	public Map<String, String> updateMemberApi(MemberDto memberDto, HttpServletResponse response) {
		
		Map<String,String> resultMap = new HashMap<>();
		
		resultMap.put("res_code", "500");
		resultMap.put("res_msg", "회원 정보 수정 중 오류가 발생하였습니다.");
		
		int result = service.updateMember(memberDto);
		
		if(result > 0) {
			// Cookie를 다루는 것은 비지니스 로직이지만, HttpServletResponse를 필요로 하기 때문에 Controller에서 다루는 것이 더 적절함
			// 쿠키(remember-me) 무효화
			Cookie rememberMe = new Cookie("remember-me", null);		// "remember-me"에 담긴 값을 null로 바꿈
			rememberMe.setMaxAge(0);									// 해당 쿠기의 길이를 0
			rememberMe.setPath("/");									// 모든 경로의 쿠기를 없애줌
			response.addCookie(rememberMe);
			
			resultMap.put("res_code", "200");
			resultMap.put("res_msg", "회원 정보 수정이 완료되었습니다.");
		}
		
		return resultMap;
		
	}
	
	@DeleteMapping("/member/{id}")
	@ResponseBody
	public Map<String, String> deleteMemberAApi(@PathVariable("id") Long memberNo, HttpServletRequest request, HttpServletResponse response) {
		
		Map<String,String> resultMap = new HashMap<>();
		
		resultMap.put("res_code", "500");
		resultMap.put("res_msg", "회원 탈퇴 중 오류가 발생하였습니다.");
		
		int result = service.deleteMember(memberNo);
		
		if(result > 0) {
			HttpSession session = request.getSession(false);		
	        if (session != null) {
	            session.invalidate();		// 서비스에서 이미 인증정보를 null 바꿨지만, 세션까지 완전히 만료시키면 더 바람직
	        }
			
			Cookie rememberMe = new Cookie("remember-me", null);
			rememberMe.setMaxAge(0);
			rememberMe.setPath("/");
			response.addCookie(rememberMe);
			
			resultMap.put("res_code", "200");
			resultMap.put("res_msg", "회원 탈퇴가 완료되었습니다.");
		}
		
		return resultMap;
	}
}
