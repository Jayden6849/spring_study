package com.gn.mvc.websocket;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gn.mvc.dto.ChatMessageDto;

import lombok.NoArgsConstructor;

@Component
@NoArgsConstructor // DB와 연결하면 Required 로 바꾸어줘야함
public class ChatWebSocketHandler extends TextWebSocketHandler {
	
	// 세션정보를 쌓을 건데, Key 에다가 누구의 정보인지 담아줄 것임 - 왜? 어떤 멤버의 채팅인지 알아야하기 때문
	private static final Map<Long, WebSocketSession> userSessions = new HashMap<>();
	
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		// 연결이 열렸을 때 동작	
		String userNo = session.getUri().getQuery().split("=")[1];		// ? 뒤에 있는 문자를 가져와서 = 기준으로 쪼개고 뒤에 있는 값
		userSessions.put(Long.parseLong(userNo), session);
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		// 클라이언트에서 서버로 msg가 넘어올 떄 동작
		
		ObjectMapper objectMapper = new ObjectMapper();
		ChatMessageDto chatMessageDto = objectMapper.readValue(message.getPayload(), ChatMessageDto.class);
		
		// 이런식으로 꺼내 쓸 수 있게 만들어줌
		// Long roomNo = chatMessageDto.getRoom_no();
		// Long senderNo = chatMessageDto.getSender_no();
		// Long receiverNo = chatMessageDto.getReceiver_no();
		// String msgContent = chatMessageDto.getMsg_content();
		
		WebSocketSession receiverSession = userSessions.get(chatMessageDto.getReceiver_no());
		
		// 받는 사람이 존재하고, 받는 사람과의 소켓이 열려있을 때
		if(receiverSession != null && receiverSession.isOpen()) {
			receiverSession.sendMessage(new TextMessage(chatMessageDto.getMsg_content()));
		}
		
		WebSocketSession senderSession = userSessions.get(chatMessageDto.getSender_no());

		// 보내는 사람이 존재하고, 보내는 사람과의 소켓이 열려있을 때 - 내가 썼지만 나도 뭐라고 썼는지 볼 수 있어야함
		if(senderSession != null && senderSession.isOpen()) {
			senderSession.sendMessage(new TextMessage(chatMessageDto.getMsg_content()));
		}
		
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		// 연결이 끊겼을 때 동작
		String userNo = session.getUri().getQuery().split("=")[1];		// ? 뒤에 있는 문자를 가져와서 = 기준으로 쪼개고 뒤에 있는 값
		userSessions.remove(Long.parseLong(userNo));
	}
	
}
