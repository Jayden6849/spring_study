package com.gn.mvc.websocket;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import lombok.NoArgsConstructor;

@Component
@NoArgsConstructor
public class BasicWebSocketHandler extends TextWebSocketHandler {
	
	// 해당 클래스에서만 사용할 목적의 전역 필드 - 웹소켓에서는 static 이 효율적 - 여러 개의 세션을 담아주기 위함 - 여러 브라우저와의 소통
	private static final List<WebSocketSession> sessionList = new ArrayList<>();	
	
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		// 새로운 웹소켓이 연결(open)된 순간 동작하는 메소드
		
		// System.out.println("서버 : 연결");
		sessionList.add(session);
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		// 클라이언트 -> 서버 : 메시지를 보낸(send) 순간 동작하는 메소드
		
		String payload = message.getPayload();
		// System.out.println("서버 : 메시지 받음 - "+payload);
		
		for(WebSocketSession temp : sessionList) {
			temp.sendMessage(new TextMessage(payload));
		}
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		// 웹소켓 연결이 끊겼을(close) 때 동작하는 메소드
		
		// System.out.println("서버 : 연결 끊김");
		sessionList.remove(session);
	}
	
}
