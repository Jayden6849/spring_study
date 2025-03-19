package com.gn.mvc.service;

import java.io.File;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.gn.mvc.dto.AttachDto;
import com.gn.mvc.entity.Attach;
import com.gn.mvc.entity.Board;
import com.gn.mvc.repository.AttachRepository;
import com.gn.mvc.repository.BoardRepository;
import com.gn.mvc.specification.AttachSpecification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AttachService {

	@Value("${ffupload.location}")
	private String fileDir;

	private final BoardRepository boardRepository;
	private final AttachRepository attachRepository;

	public AttachDto uploadFile(MultipartFile file) {
		AttachDto attachDto = new AttachDto();

		try {
			// 1. 파일이 정상적인 상태인지 파악 - 비어있는 파일은 아닌지

			if (file == null || file.isEmpty()) {
				throw new Exception("존재하지 않는 파일입니다.");
			}

			// 2. 파일 최대 용량을 확인 - Spring의 MultipartFile에서 최대로 허용하는 파일의 용량 = 1MB = 1024KB =
			// 1024*1024 byte (1048576)
			// 물론 최대로 허용하는 용량을 바꿔줄 수는 있음 - application.properties에 작성하면 됨

			long fileSize = file.getSize();

			if (fileSize >= 1048576) { // 소수점 때문에 만약을 위해 같을때까지 포함시켜서 예외 발생
				throw new Exception("허용 용량을 초과한 파일입니다.");
			}

			// 3. 파일 최초 이름 읽어와서 Dto에 넣어주기
			String oriName = file.getOriginalFilename();
			attachDto.setOri_name(oriName);

			// 4. 파일 확장자 자르기
			String fileExt = oriName.substring(oriName.lastIndexOf("."), oriName.length());

			// 5. 파일 명칭에 UUID 적용하기 - 같은 이름의 파일이름을 걸러내기 위함임 = UNIQUE 처럼 활용
			UUID uuid = UUID.randomUUID();

			// 6. uuid의 8자리마다 반복되는 하이픈(-) 제거
			String uniqueName = uuid.toString().replace("-", "");

			// 7. 새로운 파일 이름 생성해서 Dto에 넣어주기
			String newName = uniqueName + fileExt;
			attachDto.setNew_name(newName);

			// 8. 파일 저장 경로 설정해서 Dto에 넣어주기 - C:/upload/board/"newName"
			String downDir = fileDir + "board/" + newName;
			attachDto.setAttach_path(downDir);

			// 9. 파일 껍데기 생성
			File saveFile = new File(downDir);

			// 10. 경로의 존재유무 확인하고 없으면 생성
			if (saveFile.exists() == false) {
				saveFile.mkdirs();
			}

			// 11. 껍데기에 실제 파일 정보를 복제
			file.transferTo(saveFile);

		} catch (Exception e) {
			attachDto = null;
			e.printStackTrace();
		}

		return attachDto;
	}

	public List<Attach> selectAttachList(Long boardNo) {

		/*
		 * 왜? Attach 엔티티에는 boardNo 라는 필드가 없음 - 그렇다고 엔티티를 막 수정할 수는 없음 1. boardNo 기준 Board
		 * 엔티티를 조회 2. Specification 생성(Attach) 3. fineAll 메소드에 전달(spec)
		 */

		Board board = boardRepository.findById(boardNo).orElse(null);

		Specification<Attach> spec = (root, query, criteriaBuilder) -> null;
		spec = spec.and(AttachSpecification.boardEquals(board));

		return attachRepository.findAll(spec);

	}

	public Attach selectAttachOne(Long attachNo) {
		return attachRepository.findById(attachNo).orElse(null);
	}

	public int deleteFileData(Long attach_no) {
		// 파일 자체를 메모리에서 삭제하는 로직

		int result = 0;

		try {
			Attach target = attachRepository.findById(attach_no).orElse(null);

			if (target != null) {
				// attach_no 기준으로 조회해서 attach 객체가 조회된다면? (메타데이터가 있다면)

				File file = new File(target.getAttachPath()); // 해당 객체의 경로를 대상으로하여

				if (file.exists() == true) { // 서버에서 해당 파일을 삭제함
					file.delete();
				}
			}

			result = 1;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public int deleteMetaData(Long attach_no) {
		// 파일의 메타 데이터를 DB에서 삭제하는 로직

		int result = 0;

		try {
			Attach target = attachRepository.findById(attach_no).orElse(null);

			if (target != null) {
				attachRepository.delete(target); // JPA가 권장하는 방식 - 메소드를 호출할 때 매개변수로 Entity 사용하는 방식으로 진행
			}

			result = 1;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

}
