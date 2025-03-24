package com.gn.mvc.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gn.mvc.entity.ChatMsg;
import com.gn.mvc.entity.ChatRoom;
import com.gn.mvc.service.ChatMsgService;
import com.gn.mvc.service.ChatRoomService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/chat")				// 해당 클래스에서 사용하는 url은 모두 /chat 으로 시작할 것임
@RequiredArgsConstructor
public class ChatController {
	
	private final ChatRoomService chatRoomService;
	private final ChatMsgService chatMsgService;
	
	@GetMapping("/list")				// 즉, 해당 메소드의 url은 /chat/list
	public String selectChatRoomAll(Model model) {
		// 내가 소속되어 있는 모든 채팅방 목록을 보여줄 메소드
		
		List<ChatRoom> resultList = chatRoomService.selectChatRoomAll();
		model.addAttribute("chatRoomList", resultList);
		
		return "chat/list";
	}
	
	@GetMapping("/{id}/detail")
	public String selectChatRoomOne(@PathVariable("id") Long roomNo, Model model) {
		ChatRoom chatRoom = chatRoomService.selectChatRoomOne(roomNo);
		
		List<ChatMsg> msgList = chatMsgService.selectChatMsgAll(roomNo);
		
		model.addAttribute("chatRoom", chatRoom);
		model.addAttribute("msgList", msgList);
		
		return "chat/detail";
	}
	
}
