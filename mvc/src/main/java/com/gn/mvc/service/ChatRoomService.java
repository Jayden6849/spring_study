package com.gn.mvc.service;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.gn.mvc.entity.ChatRoom;
import com.gn.mvc.repository.ChatRoomRepository;
import com.gn.mvc.security.MemberDetails;
import com.gn.mvc.specification.ChatRoomSpecification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
	
	private final ChatRoomRepository repository;
	
	public List<ChatRoom> selectChatRoomAll() {
		// 세션의 정보를 가져와서 Memberdetails 를 꺼내옴 - security를 사용하는 이상 세션정보를 어디에서든 꺼내올 수 있음
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		// 반환형이 Object라서 다운캐스팅해줘야함
		MemberDetails md = (MemberDetails) authentication.getPrincipal();
		
		Specification<ChatRoom> spec = (room, query, criteriaBuilder) -> null;
		spec = spec.and(ChatRoomSpecification.fromMemberEquals(md.getMember()));	// where from_member = #{md.Member}
		spec = spec.or(ChatRoomSpecification.toMemberEquals(md.getMember()));		// where to_member = #{md.Member}
		
		List<ChatRoom> resultList = repository.findAll(spec);
		
		return resultList;	
	}
	
	public ChatRoom selectChatRoomOne(Long roomNo) {
		return repository.findById(roomNo).orElse(null);																																																																																	
	}
}
