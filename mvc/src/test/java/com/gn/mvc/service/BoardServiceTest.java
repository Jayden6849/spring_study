package com.gn.mvc.service;

// import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.gn.mvc.entity.Board;

@SpringBootTest
class BoardServiceTest {
	
	@Autowired
	private BoardService service;
	
	@Test
	void selectBoardOne_success() {
		
		// 성공테스트
		
		// 1. 예상 데이터 - boardNo 6번으로 selectBoardOne 을 시행했을 때 boardTitle 이 "월요일" 일 것이다.
		Long id = 6L;
		Board expected = Board.builder().boardTitle("월요일").build();
		
		// 2. 실제 데이터
		Board real = service.selectBoardOne(id);
		
		// 3. 비교 및 검증 - asertEquals(기대값, 실제값);
		Assertions.assertEquals(expected.getBoardTitle(), real.getBoardTitle());
		// assertEquals(expected.getBoardTitle(), real.getBoardTitle());
		
	}
	
	@Test
	void selectBoardOne_fail() {
		
		// 실패 테스트 - 존재하지 않는 PK기준으로 조회 요청
		
		Long id = 1000L;
		Board expected = null;
		
		Board real = service.selectBoardOne(id);
		
		Assertions.assertEquals(expected, real);
		
	}
}
