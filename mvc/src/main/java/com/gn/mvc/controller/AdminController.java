package com.gn.mvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")		// 공통적으로 모두 쓸 url을 적어줄 수도 있음
public class AdminController {
	
	@GetMapping("/read")		// 따라서 "/admin/read" 와 동일함
	public String adminPage() {
		return "admin/home";
	}
	
}
