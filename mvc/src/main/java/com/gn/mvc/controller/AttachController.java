package com.gn.mvc.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.gn.mvc.entity.Attach;
import com.gn.mvc.service.AttachService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AttachController {
	
	private Logger logger = LoggerFactory.getLogger(AttachController.class);
	
	private final AttachService service;
	
	@GetMapping("/download/{id}")
	public ResponseEntity<Object> fileDownload(@PathVariable("id") Long attachNo) {
		
		logger.debug("파일 다운로드 id 넘어오는지 확인 : "+attachNo);
		
		try {
			
        /*	1. id를 기준으로 파일 메타 데이터를 조회함
			2. 만약 해당하는 파일이 없다면 NotFound 예외(404)를 발생시킴 
			3. 파일 경로로 가서 바깥쪽에 있는 파일을 안쪽으로 읽기(InputStream)
			4. 한글 파일명을 인코딩할 필요가 있음 
			5. 브라우저에게 파일의 정보를 전달 */
			
			Attach fileData = service.selectAttachOne(attachNo);				// 1
			
			if(fileData == null) {												// 2
				return ResponseEntity.notFound().build();
			}
			
			Path filePath = Paths.get(fileData.getAttachPath());				// 3
			Resource resource = new InputStreamResource(Files.newInputStream(filePath));
			
			String oriFileName = fileData.getOriName();							// 4
			String encodedName = URLEncoder.encode(oriFileName, StandardCharsets.UTF_8);
			
			HttpHeaders headers = new HttpHeaders();							// 5
			headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+encodedName);
			
			return new ResponseEntity<>(resource, headers, HttpStatus.OK);
			
		} catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().build();		// 400에러를 발생시킴
		}
		
	}
	
}
