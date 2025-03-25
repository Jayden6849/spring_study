package com.gn.todo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gn.todo.dto.TodoDto;

@Controller
public class TodoController {

	@PostMapping("/todo")
	@ResponseBody
	public String createTodoApi(TodoDto dto) {
		
		System.out.println(dto);
		
		
		return "home";
	}
	
	
}
