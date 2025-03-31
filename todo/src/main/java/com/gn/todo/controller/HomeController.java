package com.gn.todo.controller;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.gn.todo.dto.PageDto;
import com.gn.todo.dto.SearchDto;
import com.gn.todo.entity.Attach;
import com.gn.todo.entity.Todo;
import com.gn.todo.service.AttachService;
import com.gn.todo.service.TodoService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class HomeController {
	
	private final TodoService todoService;
	private final AttachService attachService;
	
	@GetMapping({"", "/", "/todo"})
	public String homeView(Model model, PageDto pageDto, SearchDto searchDto) {
		
		if (pageDto.getNowPage() == 0) {
			pageDto.setNowPage(1);
		}
		
		Page<Todo> todoList = todoService.selectTodoAll(pageDto, searchDto);
		
		List<Attach> attachList = attachService.selectAttachList();
		
		model.addAttribute("attachList", attachList);
		
		if(todoList != null) {
			pageDto.setTotalPage(todoList.getTotalPages());	
		}
		
		model.addAttribute("todoList", todoList);
		model.addAttribute("pageDto", pageDto);
		model.addAttribute("searchDto", searchDto);
		
		return "home";
		
	}
	
}
