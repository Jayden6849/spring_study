package com.gn.mvc.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gn.mvc.dto.BoardDto;
import com.gn.mvc.service.BoardService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class BoardController {
	
	// 1. 필드 주입 방법 - 순환참조 문제에 취약함 - new 라는 단어만 안 썼지 사실 쓴 것과 다르지 않음
//	@Autowired
//	BoardService service;
	
	// 2. 메소드 주입 방법 - 불변성이 보장되지 않는 문제가 있음
//	private BoardService service;
	
//	@Autowired
//	public void setBoardService(BoardService service) {
//		this.service = service;
//	}
	
	// 3. 생성자 주입 방법 - 권장
	private final BoardService service;
	
//	@Autowired
//	public BoardController(BoardService service) {
//		this.service = service;
//	}
	
	@GetMapping("/board/create")
	public String createBoardView() {
		return "board/create";
	}
	
	@PostMapping("/board")
	@ResponseBody
	public Map<String, String> createBoardApi(
//			첫 번 째 방법
//			@RequestParam("board_title") String boardTitle,
//			@RequestParam("board_content") String boardContent
			
//			두 번 째 방법
//			@RequestParam Map<String, String> param
			BoardDto dto
	) {
		Map<String, String> resultMap = new HashMap<>();
		
		resultMap.put("res_code", "500");
		resultMap.put("res_msg", "게시글 등록 중 오류가 발생하였습니다.");
		
		// 사용자로부터 입력받은 dto를 지니고 있는 상태임
		// Service 가 가지고 있는 createBoard() 를 호출해야함
		BoardDto resultDto = service.createBoard(dto);
		
		if(resultDto != null) {
			resultMap.put("res_code", "200");
			resultMap.put("res_msg", "게시글 등록이 완료되었습니다.");
		}
		
		return resultMap;
		
	}
	
}
