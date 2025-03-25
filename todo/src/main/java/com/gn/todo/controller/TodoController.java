package com.gn.todo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gn.todo.dto.TodoDto;
import com.gn.todo.entity.Todo;
import com.gn.todo.service.TodoService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class TodoController {
	
	private final TodoService service;
	
	@PostMapping("/todo")
	@ResponseBody
	public Map<String, String> createTodoApi(TodoDto dto) {
		
		Todo todo = service.createTodo(dto);
		
		Map<String, String> resultMap = new HashMap<>();

		resultMap.put("res_code", "500");
		resultMap.put("res_msg", "할 일 추가 중 오류가 발생하였습니다.");
		
		if(todo != null) {
			resultMap.put("res_code", "200");
			resultMap.put("res_msg", "할 일이 정상적으로 추가되었습니다.");
		}
		
		return resultMap;
		
	}
	
}
