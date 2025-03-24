package com.gn.mvc.service;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.gn.mvc.entity.ChatMsg;
import com.gn.mvc.entity.ChatRoom;
import com.gn.mvc.repository.ChatMsgRepository;
import com.gn.mvc.repository.ChatRoomRepository;
import com.gn.mvc.specification.ChatMsgSpecification;
import com.gn.mvc.specification.ChatRoomSpecification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatMsgService {
	
	private final ChatRoomRepository chatRoomRepository;
	private final ChatMsgRepository chatMsgRepository;
	
	/*
	 * author : Jayden
	 * history : 2025-03-24
	 * param :roomNo
	 * return : List<ChatMsg>
	 * role(purpose) : Select ChatMsg List
	 */
	public List<ChatMsg> selectChatMsgAll(Long roomNo) {
		
		// (1) 전달받은 id를 기준으로 chatRoom Entity를 조회
		// (2) ChatRoom Entity를 기준으로 spec을 생성
		// (3) spec을 매개변수로 전달하여 findAll() 을 반환받음
		
		ChatRoom chatRoom = chatRoomRepository.findById(roomNo).orElse(null);
		
		Specification<ChatMsg> spec = (room, query, criteriaBuilder) -> null;
		spec = spec.and(ChatMsgSpecification.selectChatMsgAll(chatRoom));
		
		return chatMsgRepository.findAll(spec);
		
	}
	
}
