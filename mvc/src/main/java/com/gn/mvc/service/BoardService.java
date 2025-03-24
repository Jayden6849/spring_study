package com.gn.mvc.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gn.mvc.dto.AttachDto;
import com.gn.mvc.dto.BoardDto;
import com.gn.mvc.dto.PageDto;
import com.gn.mvc.dto.SearchDto;
import com.gn.mvc.entity.Attach;
import com.gn.mvc.entity.Board;
import com.gn.mvc.repository.AttachRepository;
import com.gn.mvc.repository.BoardRepository;
import com.gn.mvc.specification.AttachSpecification;
import com.gn.mvc.specification.BoardSpecification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardService {

	private final BoardRepository repository;
	private final AttachRepository attachRepository;
	private final AttachService attachService;

	@Transactional(rollbackFor = Exception.class)
	public int createBoard(BoardDto dto, List<AttachDto> attachList) {
		int result = 0;

		try {

			/*
			 * 1. Board 엔티티를 insert 해줘야함 2. insert 결과로 반환받은 pk를 알아야함 3. attachList에 데이터가 있는
			 * 경우에는 Attach 엔티티도 insert 해줘야함 - 즉, 트랜잭션이 필요함
			 */

			Board entity = dto.toEntity();
			Board saved = repository.save(entity);

			Long boardNo = saved.getBoardNo();

			if (attachList.size() != 0) { // attachList가 있는 경우 = 첨부파일이 있는 경우
				for (AttachDto attachDto : attachList) {
					attachDto.setBoard_no(boardNo); // 해당 board_no를 셋해주고
					Attach attach = attachDto.toEntity(); // 엔티티로 바꾸어준 후
					attachRepository.save(attach); // `attach` 테이블에 인서트 함
				}
			}

			result = 1;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;

		// 1. dto에 담긴 데이터를 entity에 옮겨담아줘야함 - DTO 에서 처리할 것
//		Board param = dto.toEntity();

		// 2. Repository의 save() 메소드를 호출해야함
//		Board result = repository.save(param);

		// 3. DB와 직접 소통하는 부분이 아니면 DTO로 반환하는 것이 적합함
//		return new BoardDto().toDto(result);

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
		
		Specification<Board> spec = (root, query, criteriaBuilder) -> null;

		Pageable pageable = PageRequest.of(pageDto.getNowPage() - 1, pageDto.getNumPerPage(),
				Sort.by("regDate").descending());

		if (searchDto.getOrder_type() == 2) {
			pageable = PageRequest.of(pageDto.getNowPage() - 1, pageDto.getNumPerPage(),
					Sort.by("regDate").ascending());
		}
		
		if (searchDto.getSearch_type() == 1) {
			spec = spec.and(BoardSpecification.boardTitleContains(searchDto.getSearch_text()));
		} else if (searchDto.getSearch_type() == 2) {
			spec = spec.and(BoardSpecification.boardContentContains(searchDto.getSearch_text()));
		} else if (searchDto.getSearch_type() == 3) {
			spec = spec.and(BoardSpecification.boardTitleContains(searchDto.getSearch_text()))
					.or(spec.and(BoardSpecification.boardContentContains(searchDto.getSearch_text())));
		} else {
			// spec == null 이기 때문에 findAll()을 쓴 것과 같음
		}
		Page<Board> resultList = repository.findAll(spec, pageable);

		return resultList;

	}

	public Board selectBoardOne(Long id) {
		return repository.findById(id).orElse(null); // null 인 경우는 그냥 null로 반환해라. 닫지 않고 Optional<T>로 열어놓을 수도 있지만 복잡함.
	}

	@Transactional(rollbackFor = Exception.class)
	public int updateBoard(BoardDto boardDto, List<AttachDto> attachList) {
		int result = 0;

//		JPA에서 권고하는 방식 - 한 번 더 조회해서 수정하게 되므로 무결성이 높아짐
//		1. @Id를 쓴 필드를 기준을 타겟 조회
//		2. 타겟이 존재하는 경우에만 업데이트를 행함

//		기본적으로 자식을 먼저 수정하고 그 다음에 부모를 수정하는 것이 논리상 맞음
//		3. 파일이 존재하는 경우
//		(1) 메모리에서 파일 자체를 삭제
//		(2) DB에서 메타 데이터 삭제

		try {

			Board target = repository.findById(boardDto.getBoard_no()).orElse(null);

			if (target != null) {

				if (boardDto.getDelete_files() != null && !boardDto.getDelete_files().isEmpty()) {
					// 삭제하고자 하는 파일이 존재하는 경우 - 파일이 먼저 삭제 되어야함

					for (Long attach_no : boardDto.getDelete_files()) {
						// 메모리에서 파일 삭제
						int delFile = attachService.deleteFileData(attach_no);

						if (delFile > 0) {
							// DB에서 메타데이터 삭제
							int delMeta = attachService.deleteMetaData(attach_no);

							if (delMeta > 0) {
								// 여기까지 오면 boardDto.delete_files()에 들어있는 파일 삭제는 적절하게 완료되었음
								// 기타 수정사항이나 파일이 수정되어야하는 경우 (파일을 새롭게 추가하는 로직이 필요함)

								Board entity = boardDto.toEntity();
								Board saved = repository.save(entity);

								Long boardNo = saved.getBoardNo();

								if (attachList.size() != 0) { // attachList가 있는 경우 = 첨부파일이 있는 경우
									for (AttachDto attachDto : attachList) {
										attachDto.setBoard_no(boardNo); // 해당 board_no를 셋해주고
										Attach attach = attachDto.toEntity(); // 엔티티로 바꾸어준 후
										attachRepository.save(attach); // `attach` 테이블에 인서트 함
									}
								}

								result = 1;
							}
						}
					}
				} else {
					// 삭제하고자 하는 파일이 존재하지 않는 경우 - 게시글 수정 or 파일 추가만 작동하면 됨

					Board entity = boardDto.toEntity();
					Board saved = repository.save(entity);

					Long boardNo = saved.getBoardNo();

					if (attachList.size() != 0) { // attachList가 있는 경우 = 첨부파일이 있는 경우
						for (AttachDto attachDto : attachList) {
							attachDto.setBoard_no(boardNo); // 해당 board_no를 셋해주고
							Attach attach = attachDto.toEntity(); // 엔티티로 바꾸어준 후
							attachRepository.save(attach); // `attach` 테이블에 인서트 함
						}
					}

					result = 1;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	@Transactional(rollbackFor = Exception.class)
	public int deleteBoard(Long boardNo) {
		int result = 0;

		try {

			Board target = repository.findById(boardNo).orElse(null);

			if (target != null) {
				// 삭제하고자 하는 대상이 있는 경우
				
				Specification<Attach> spec = Specification.where(AttachSpecification.boardEquals(target));
				
				List<Attach> attachList = attachRepository.findAll(spec);
				
				for(Attach attach : attachList) {
					attachService.deleteFileData(attach.getAttachNo());	// 1. 서버에서 먼저 파일을 내려주고
					attachRepository.delete(attach);					// 2. DB에서 파일 메타 데이터를 내려줌
				}
				
				repository.deleteById(boardNo);							// 3. 마지막으로 게시글 데이터를 DB에서 내려줌
			}

			result = 1;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}
}
