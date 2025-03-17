package com.gn.mvc.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.gn.mvc.dto.BoardDto;
import com.gn.mvc.dto.PageDto;
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
	
	public Page<Board> selectBoardAll(SearchDto searchDto, PageDto pageDto) {
		
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
		
		/*
		 * Sort sort = Sort.by("regDate").descending();
		 * 
		 * if(searchDto.getOrder_type() == 2) { sort = Sort.by("regDate").ascending(); }
		 */
		
		Pageable pageable = PageRequest.of(pageDto.getNowPage()-1, pageDto.getNumPerPage(), Sort.by("regDate").descending());
		
		if(searchDto.getOrder_type() == 2) {
			pageable = PageRequest.of(pageDto.getNowPage()-1, pageDto.getNumPerPage(), Sort.by("regDate").ascending());
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
		Page<Board> resultList = repository.findAll(spec, pageable);
		
		return resultList;
		
	}
	
	public Board selectBoardOne(Long id) {
		return repository.findById(id).orElse(null); 	// null 인 경우는 그냥 null로 반환해라. 닫지 않고 Optional<T>로 열어놓을 수도 있지만 복잡함.
	}
	
	public Board updateBoard(BoardDto boardDto) {
		Board result = null;
		
		/* 
		 * JPA에서 권고하는 방식 - 한 번 더 조회해서 수정하게 되므로 무결성이 높아짐
		 * 1. @Id를 쓴 필드를 기준을 타겟 조회
		 * 2. 타겟이 존재하는 경우에만 업데이트를 행함
		 */
		
		Board target = repository.findById(boardDto.getBoard_no()).orElse(null);
		
		if(target != null) {
			result = repository.save(boardDto.toEntity());
		}
		
		return result;
	}
}
