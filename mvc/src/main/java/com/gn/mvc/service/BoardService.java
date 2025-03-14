package com.gn.mvc.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.gn.mvc.dto.BoardDto;
import com.gn.mvc.entity.Board;
import com.gn.mvc.repository.BoardRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardService {
	
	private final BoardRepository repository;
	
	public BoardDto createBoard(BoardDto dto) {	
		// 1. dto에 담긴 데이터를 entity에 옮겨담아줘야함 - DTO 에서 처리할 것
		Board param = dto.toEntity();
		
		// 2. Repository의 save() 메소드를 호출해야함
		Board result = repository.save(param);
		
		// 3. DB와 직접 소통하는 부분이 아니면 DTO로 반환하는 것이 적합함
		return new BoardDto().toDto(result);	
	}
	
	public List<Board> selectBoardAll() {
		return repository.findAll();
	}
	
}
