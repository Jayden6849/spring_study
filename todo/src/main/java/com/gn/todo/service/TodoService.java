package com.gn.todo.service;

import org.springframework.stereotype.Service;

import com.gn.todo.dto.TodoDto;
import com.gn.todo.entity.Todo;
import com.gn.todo.repository.TodoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TodoService {
	
	private final TodoRepository repository;
	
	public Todo createTodo(TodoDto dto) {
		
		Todo result = repository.save(dto.toEntity());
		
		return result;
	}
	
}
