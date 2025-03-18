package com.gn.mvc.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.gn.mvc.dto.AttachDto;
import com.gn.mvc.dto.BoardDto;
import com.gn.mvc.dto.PageDto;
import com.gn.mvc.dto.SearchDto;
import com.gn.mvc.entity.Attach;
import com.gn.mvc.entity.Board;
import com.gn.mvc.service.AttachService;
import com.gn.mvc.service.BoardService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class BoardController {
	
	private Logger logger = LoggerFactory.getLogger(BoardController.class);
	
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
	private final AttachService attachService;
	
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
//		BoardDto resultDto = service.createBoard(dto);
		
		/*
		 * logger.debug("1 : "+resultDto.toString());
		 * logger.info("2 : "+resultDto.toString());
		 * logger.warn("3 : "+resultDto.toString());
		 * logger.error("4 : "+resultDto.toString());
		 */
		
//		if(resultDto != null) {
//			resultMap.put("res_code", "200");
//			resultMap.put("res_msg", "게시글 등록이 완료되었습니다.");
//		}
		
		List<AttachDto> attachDtoList = new ArrayList<>();
		
		for(MultipartFile mf : dto.getFiles()) {
			logger.debug(mf.getOriginalFilename());
			
			AttachDto attachDto = attachService.uploadFile(mf);
			
			if(attachDto != null) {									// 서버에 파일이 정상적으로 업로드된 시점
				attachDtoList.add(attachDto);
			}	
		}
		
		if(dto.getFiles().size() == attachDtoList.size()) { 		// 서버에도 업로드 되었고, ArrayList에도 정상적으로 add된 시점
			int result = service.createBoard(dto, attachDtoList);
			
			if(result > 0) {
				resultMap.put("res_code", "200");
				resultMap.put("res_msg", "게시글 등록이 완료되었습니다.");
			}
		}
		
		return resultMap;
	}
	
	@GetMapping("/board")
	public String selectBoardAll(Model model, SearchDto searchDto, PageDto pageDto) {
		
		if(pageDto.getNowPage() == 0) {											// 0. 0페이지는 없음. 1페이지가 시작임
			pageDto.setNowPage(1);
		}
		
		Page<Board> resultList = service.selectBoardAll(searchDto, pageDto); 	// 1. DB에서 목록을 SELECT
		
		pageDto.setTotalpage(resultList.getTotalPages());
		
		model.addAttribute("boardList", resultList);							// 2. 목록 MODEL에 등록
		model.addAttribute("searchDto", searchDto);
		model.addAttribute("pageDto", pageDto);
		
		// 3. list.html에 데이터 세팅
		return "board/list";
	}
	
	@GetMapping("/board/{id}")
	public String selectBoardOne(@PathVariable("id") Long id, Model model) {
		logger.info("게시글 단일 조회 : "+id);
		
		Board result = service.selectBoardOne(id);
		logger.info("id 기준 상세 조회 : "+result);
		
		List<Attach> attachList = attachService.selectAttachList(id);
		
		model.addAttribute("board", result);
		model.addAttribute("attachList", attachList);
		
		return "board/detail";
	}
	
	@GetMapping("/board/{id}/update")
	public String updateBoardView(@PathVariable("id") Long id, Model model) {
		Board board = service.selectBoardOne(id); 						// 해당 게시글의 데이터를 넘겨주기 위함
		
		List<Attach> attachList = attachService.selectAttachList(id);	// 파일에 대한 메타데이터도 같이 넘겨주기 위함
		
		model.addAttribute("board", board);
		model.addAttribute("attachList", attachList);
		
		return "board/update";
	}
	
	@PostMapping("/board/{id}/update")
	@ResponseBody
	public Map<String, String> updateBoardApi(BoardDto boardDto) {
		// 1. boardDto 출력해보기
//		logger.debug("boardDto 출력확인 : "+boardDto.toString());
		
		// 2. BoardService -> BoardRepository 게시글 수정
//		Board result = service.updateBoard(boardDto);
		
//		logger.debug("entity 출력확인 : "+result.toString());
		
		logger.warn("삭제 파일 정보 : " + boardDto.getDelete_files());
		
		// 3. 수정 결과 Entity르 ㄹ받아와서 null 이 아니면 성공, 그 외에는 실패
		Map<String, String> resultMap = new HashMap<>();
		
		resultMap.put("res_code", "500");
		resultMap.put("res_msg", "게시글 수정 중 오류가 발생하였습니다.");
		
		/*
		 * if(result != null) { resultMap.put("res_code", "200");
		 * resultMap.put("res_msg", "게시글 수정이 완료되었습니다."); }
		 */
		
		return resultMap;
	}
	
	@DeleteMapping("/board/{id}")
	@ResponseBody
	public Map<String, String> deleteBoardApi(@PathVariable("id") Long boardNo) {
		
		logger.debug("게시글 삭제 - boardDto 출력확인 : "+boardNo.toString());
		
		Map<String, String> resultMap = new HashMap<>();
		
		resultMap.put("res_code", "500");
		resultMap.put("res_msg", "게시글 삭제 중 오류가 발생하였습니다.");
		
		int result = service.deleteBoard(boardNo);
		
		if(result > 0) {
			resultMap.put("res_code", "200");
			resultMap.put("res_msg", "게시글이 삭제되었습니다.");
		}
		
		return resultMap;
	}
	
}
