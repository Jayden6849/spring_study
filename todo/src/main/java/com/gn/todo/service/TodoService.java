package com.gn.todo.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.gn.todo.dto.PageDto;
import com.gn.todo.dto.SearchDto;
import com.gn.todo.dto.TodoDto;
import com.gn.todo.entity.Todo;
import com.gn.todo.repository.TodoRepository;
import com.gn.todo.specification.TodoSpecification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TodoService {
	
	private final TodoRepository repository;
	
	// 할 일을 추가하는 로직
	public Todo createTodo(TodoDto dto) {
		Todo result = repository.save(dto.toEntity());
		return result;
	}
	
	// 할 일(전체)을 조회하는 로직
	public Page<Todo> selectTodoAll(PageDto pageDto, SearchDto searchDto) {
		
		Specification<Todo> spec = (root, query, criteriaBuilder) -> null;
		spec = spec.and(TodoSpecification.contentContains(searchDto.getSearch_text()));
		
		Pageable pageable = PageRequest.of(pageDto.getNowPage() - 1, pageDto.getNumPerPage(),
				Sort.by("no").ascending());
		
		Page<Todo> resultList = repository.findAll(spec, pageable);
		
		return resultList;
	}
	
	// 할 일을 수정하는 로직
	public Todo updateTodo(TodoDto dto) {
		Todo target = repository.findById(dto.getNo()).orElse(null);
		Todo saved = null;
		
		if(target != null) {
			dto.setContent(target.getContent());
			saved = repository.save(dto.toEntity());
		}
		
		return saved;
	}
	
	// 할 일을 삭제하는 로직
	public int deleteTodo(Long no) {
		
		int result = 0;
		
		try {
			Todo target = repository.findById(no).orElse(null);
			
			if(target != null) {
				repository.delete(target);
				
				result = 1;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
}
