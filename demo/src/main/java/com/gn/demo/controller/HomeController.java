package com.gn.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import com.gn.demo.vo.Member;

@Controller
public class HomeController {
	
	@GetMapping({"", "/"})
	public String home() {
		return "home";
	}
	
	@GetMapping("/test")
	public String test(Model model) {
		model.addAttribute("name", "김철수");
		model.addAttribute("age", 25);
		return "test";
	}
	
	@GetMapping("/testView")
	public ModelAndView testView() {
		ModelAndView mav = new ModelAndView();
		
		mav.setViewName("modelAndView");		// 어디로 갈 지
		mav.addObject("name", "이영희");			// 어떤 데이터를 담아서 갈 지
		mav.addObject("age",20);
		
		return mav;
	}
	
	@GetMapping("/bye")
	public String bye(Model model) {
		model.addAttribute("member", new Member("홍길동", 50));
		
		return "bye";
	}
	
}

// 규칙 - src/main/resources/templates 안에 home.html 파일이 존재하여야함 - 타임리프는 html 확장자를 사용함
// 또한 WAS 도 품고있기 떄문에 굳이 서버를 지정해줄 필요도 없음 : 브라우저에 localhost:8080 쓰면 확인할 수 있음