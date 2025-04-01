package com.gn.todo.s3;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.model.S3Object;
import com.gn.todo.dto.AttachDto;
import com.gn.todo.entity.Attach;
import com.gn.todo.service.AttachService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/s3")				// 해당 클래스의 하위 메소드들의 url 항상 "/s3" 로 시작한다는 의미
@RequiredArgsConstructor
public class S3Controller {
	
	private final S3Service service;
	private final AttachService attachService;
	
	@PostMapping("/upload")
	@ResponseBody
	public Map<String, String> uploadFile(@RequestParam("files") List<MultipartFile> fileList) {		
		Map<String, String> resultMap = new HashMap<>();
		
		resultMap.put("res_code", "500");
		resultMap.put("res_msg", "파일 업로드 중 문제가 발생하였습니다.");
		
		try {
			for(MultipartFile mf : fileList) {
				// s3Service를 통해 파일을 업로드
				AttachDto dto = service.uploadFile(mf);
				
				// attachService를 통해 메타데이터 인서트
				if(dto != null) {
					attachService.createAttach(dto);
				}
			}
			
			resultMap.put("res_code", "200");
			resultMap.put("res_msg", "파일 업로드가 완료되었습니다.");
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return resultMap;
	}
	
	@GetMapping("/download/{id}")
	public ResponseEntity<Object> downlaodFile(@PathVariable("id") Long attachNo) {
		try {
			// 1. 파일 정보 조회
			Attach fileData = attachService.selectAttachOne(attachNo);
			
			if(fileData == null) {
				return ResponseEntity.notFound().build();
			}
			
			// 2. S3 서비스와 연결
			S3Object s3Object = service.getS3Object(fileData.getNewName());
			
			// 3. S3 서비스에서 컨텐츠 정보 가져오기
			InputStream inputStream = s3Object.getObjectContent();
			
			// 4. 파일 데이터를 byte[] 로 변환
			byte[] fileByte = inputStream.readAllBytes();
			
			// 5. 기존 파일 명칭을 세팅
			String OriFileName = fileData.getOriName();
			String encodedName = URLEncoder.encode(OriFileName, StandardCharsets.UTF_8);
			
			// 6. 브라우저에게 보내주기
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.parseMediaType(s3Object.getObjectMetadata().getContentType()));
			headers.setContentDispositionFormData("attachment", encodedName);
			headers.setContentLength(fileByte.length);
			
			// 7. ResponseEntity에 파일 데이터 얹어서 반환
			return new ResponseEntity<>(fileByte, headers, HttpStatus.OK);
			
		} catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().build();
		}
	}
	
}
