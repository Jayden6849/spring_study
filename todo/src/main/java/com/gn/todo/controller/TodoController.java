package com.gn.todo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
	
	@PostMapping("/todo/{id}/update")
	@ResponseBody
	public Map<String, String> updateTodoApi(@PathVariable("id") Long no, @RequestBody TodoDto dto) {
		
		dto.setNo(no);
		
		if("true".equals(dto.getFlag())) {
			dto.setFlag("Y");
		} else {
			dto.setFlag("N");
		}
		
		Todo todo = service.updateTodo(dto);
		
		Map<String, String> resultMap = new HashMap<>();

		resultMap.put("res_code", "500");
		resultMap.put("res_msg", "할 일 수정 중 오류가 발생하였습니다.");
		
		if(todo != null) {
			resultMap.put("res_code", "200");
			resultMap.put("res_msg", "할 일이 정상적으로 수정되었습니다.");
		}
		
		return resultMap;
	}
	
	@DeleteMapping("/todo/{id}/delete")
	@ResponseBody
	public Map<String, String> deleteTodoApi(@PathVariable("id") Long no) {
		
		int result = 0;
		
		result = service.deleteTodo(no);
		
		Map<String, String> resultMap = new HashMap<>();

		resultMap.put("res_code", "500");
		resultMap.put("res_msg", "할 일 삭제 중 오류가 발생하였습니다.");
		
		if(result > 0) {
			resultMap.put("res_code", "200");
			resultMap.put("res_msg", "할 일이 정상적으로 삭제되었습니다.");
		}
		
		return resultMap;
		
	}
	
}
