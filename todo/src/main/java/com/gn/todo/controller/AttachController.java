package com.gn.todo.controller;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.gn.todo.dto.AttachDto;
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
	
}
