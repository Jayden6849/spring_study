package com.gn.todo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gn.todo.entity.Todo;

public interface TodoRepository extends JpaRepository<Todo, Long> {
	
	
	
}
