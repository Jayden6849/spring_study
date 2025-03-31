package com.gn.todo.s3;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.gn.todo.dto.AttachDto;
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
	
}
