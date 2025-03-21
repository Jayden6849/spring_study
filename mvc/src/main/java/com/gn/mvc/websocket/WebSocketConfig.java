package com.gn.mvc.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {
	
	private final BasicWebSocketHandler basicWebSocketHandler;
	private final ChatWebSocketHandler chatWebSocketHandler;
	
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		
		registry
			// "/ws/basic" 경로로 WebSocket 연결을 허용 = home.html 에 적은 주소 :: let socket = new WebSocket("ws://localhost:8080/ws/basic");
			.addHandler(basicWebSocketHandler, "/ws/basic")
			// 전역 : CORS(Cross-Origin Resource Sharing) 허용 설정 - CORS 허용 설정이라고 검색하면 경로를 지정해줄 수 있는 설정값들이 있음 
			.setAllowedOrigins("http://localhost:8080");
			//	.setAllowedOrigins("*"); 라고 써도 됨
		
		registry.addHandler(chatWebSocketHandler, "/ws/chat")
				.setAllowedOrigins("http://localhost:8080");
		
	}
	
}
