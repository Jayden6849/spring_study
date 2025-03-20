package com.gn.mvc.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gn.mvc.dto.MemberDto;
import com.gn.mvc.service.MemberService;

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
		resultMap.put("result_msg", "회원가입 중 오류가 발생하였습니다.");
		
		System.out.println("크리에이트 전 : "+memberDto);
		
		MemberDto resultDto = service.createMember(memberDto);
		
		System.out.println("크리에이트 후 : "+memberDto);
		
		if(resultDto != null) {
			resultMap.put("res_code", "200");
			resultMap.put("result_msg", "회원가입이 완료되었습니다.");
		}
		
		return resultMap;
	}
	
	@GetMapping("/signup")
	public String createMemberView() {
		return "member/create";
	}
	
}
