package com.gn.todo.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.gn.todo.entity.Todo;
import com.gn.todo.service.TodoService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class HomeController {
	
	private final TodoService todoService;
	
	@GetMapping({"", "/"})
	public String homeView(Model model) {
		
		List<Todo> todoList = todoService.selectTodoAll();
		
		model.addAttribute("todoList", todoList);
		
		return "home";
	}
	
}
