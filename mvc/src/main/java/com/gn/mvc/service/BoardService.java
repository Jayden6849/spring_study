package com.gn.mvc.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.gn.mvc.dto.BoardDto;
import com.gn.mvc.dto.SearchDto;
import com.gn.mvc.entity.Board;
import com.gn.mvc.repository.BoardRepository;
import com.gn.mvc.specification.BoardSpecification;

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
	
	public List<Board> selectBoardAll(SearchDto searchDto) {
		
		/*
		 * List<Board> resultList = new ArrayList<>();
		 * 
		 * if(searchDto.getSearch_type() == 1) { // 제목을 기준으로 검색하는 상황 resultList =
		 * repository.findByTitleLike(searchDto.getSearch_text()); } else
		 * if(searchDto.getSearch_type() == 2) { // 내용을 기준으로 검색하는 상황 resultList =
		 * repository.findByContentLike(searchDto.getSearch_text()); } else
		 * if(searchDto.getSearch_type() == 3) { // 제목 + 내용을 기준으로 검색하는 상황 resultList =
		 * repository.findByTitleOrContentLike(searchDto.getSearch_text(),
		 * searchDto.getSearch_text()); } else { // 검색버튼을 누르지 않은 상황 = WHERE절 없이 검색한 것과
		 * 동일한 상황임 resultList = repository.findAll(); }
		 * 
		 * return resultList;
		 */
		
		Sort sort = Sort.by("regDate").descending();
		
		if(searchDto.getOrder_type() == 2) {
			sort = Sort.by("regDate").ascending();
		}
		
		Specification<Board> spec = (root, query, criteriaBuilder) -> null;
		
		if(searchDto.getSearch_type() == 1) {
			spec = spec.and(BoardSpecification.boardTitleContains(searchDto.getSearch_text()));
		} else if(searchDto.getSearch_type() == 2) {
			spec = spec.and(BoardSpecification.boardContentContains(searchDto.getSearch_text()));
		} else if(searchDto.getSearch_type() == 3) {
			spec = spec.and(BoardSpecification.boardTitleContains(searchDto.getSearch_text())).or(spec.and(BoardSpecification.boardContentContains(searchDto.getSearch_text())));
		} else {
			// spec == null 이기 때문에 findAll()을 쓴 것과 같음
		}
		List<Board> resultList = repository.findAll(spec, sort);
		
		return resultList;
		
	}
	
}
