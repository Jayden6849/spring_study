package com.gn.todo.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.gn.todo.dto.AttachDto;
import com.gn.todo.entity.Attach;
import com.gn.todo.service.AttachService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AttachController {
	
	private final AttachService service;
	
	@PostMapping("/attach/create")
	@ResponseBody
	public Map<String,String> createAttachApi(@RequestParam("files") List<MultipartFile> fileList) {
		Map<String, String> resultMap = new HashMap<>();
		
		resultMap.put("res_code", "500");
		resultMap.put("res_msg", "파일 업로드 중 문제가 발생하였습니다.");
		
		try {
			for(MultipartFile mf : fileList) {
				AttachDto dto = service.uploadFile(mf);
				
				if(dto != null) {
					service.createAttach(dto);
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
	public ResponseEntity<Object> fileDownload(@PathVariable("id") Long attachNo) {
		
		System.out.println("CI/CD 동작 테스트");
		
		try {
			Attach fileData = service.selectAttachOne(attachNo);
			
			// 시큐리티를 쓰고 있다고 가정을 하면, authentication.getPrincipal().getId(); 랑 fileData.getWriterNo(); 를 비교해서 권한 검사를 해야함
			// 일치하지 않으면 return ResponseEntity.forbidden().build();
			
			if(fileData == null) {
				return ResponseEntity.notFound().build();
			}
			
			Path filePath = Paths.get(fileData.getAttachPath());
			Resource resource = new InputStreamResource(Files.newInputStream(filePath));
			
			String oriFileName = fileData.getOriName();
			String encodedName = URLEncoder.encode(oriFileName, StandardCharsets.UTF_8);
			
			HttpHeaders headers = new HttpHeaders();
			headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+encodedName);
			
			return new ResponseEntity<>(resource, headers, HttpStatus.OK);
		} catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().build();
		}
	}
	
}
