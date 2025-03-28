package com.gn.todo.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.gn.todo.entity.Todo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class TodoDto {
	
	private Long no;
	private String content;
	private String flag = "N";
	
	public Todo toEntity() {
		return Todo.builder()
					.no(this.no)
					.content(this.content)
					.flag(this.flag)
					.build();
	}
	
	public TodoDto toDto(Todo todo) {
		return TodoDto.builder()
						.no(todo.getNo())
						.content(todo.getContent())
						.flag(todo.getFlag())
						.build();
	}
	
}
