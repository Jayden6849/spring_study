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
import com.gn.mvc.entity.ChatMsg;
import com.gn.mvc.entity.ChatRoom;
import com.gn.mvc.entity.Member;
import com.gn.mvc.repository.ChatMsgRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {
	
	private final ChatMsgRepository chatMsgRepository;
	
	// 세션정보를 쌓을 건데, Key 에다가 누구의 정보인지 담아줄 것임 - 왜? 어떤 멤버의 채팅인지 알아야하기 때문
	private static final Map<Long, WebSocketSession> userSessions = new HashMap<>();
	private static final Map<Long, Long> userRooms = new HashMap<>();
	
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		// 연결이 열렸을 때 동작	
		// String userNo = session.getUri().getQuery().split("=")[1];		// ? 뒤에 있는 문자를 가져와서 = 기준으로 쪼개고 뒤에 있는 값
		
		String userNo = getQueryParam(session, "senderNo");
		String roomNo = getQueryParam(session, "roomNo");
		
		userSessions.put(Long.parseLong(userNo), session);
		userRooms.put(Long.parseLong(userNo), Long.parseLong(roomNo));
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
		
		// 데이터베이스에 채팅 메시지를 등록하는 절차 =========================================
		// 1. Dto -> entity :: 일회성이기때문에 DTO에 따로 안 만들고 바로 만들어서 쓰는 것임

		if(userSessions.containsKey(chatMessageDto.getSender_no())) {
			// 네트워크 상 문제가 없을 경우에만 로직이 동작하도록
			
			Member member = Member.builder()
					.memberNo(chatMessageDto.getSender_no())
					.build();

			ChatRoom chatRoom = ChatRoom.builder()
										.roomNo(chatMessageDto.getRoom_no())
										.build();

			ChatMsg entity = ChatMsg.builder()
									.sendMember(member)
									.chatRoom(chatRoom)
									.msgContent(chatMessageDto.getMsg_content())
									.build();

			// 2. save(entity)
			ChatMsg result = chatMsgRepository.save(entity);
			
			if(result != null) {
				// 메시지가 잘 전송된 경우 :: 메시지가 DB에 저장된 경우
				
			} else {
				// 메시지가 전송되지 않은 경우 :: 메시지가 DB에 저장되지 않은 경우
				
			}
			
		}
		
		// =============================================================================
		
		// 채팅을 구현하여 프론트로 넘기는 절차 ==============================================
		WebSocketSession receiverSession = userSessions.get(chatMessageDto.getReceiver_no());
		Long receiverRoom = userRooms.get(chatMessageDto.getReceiver_no());
		
		// 받는 사람이 존재하고, 받는 사람과의 소켓이 열려있으며, 방 번호가 일치할 때
		if(receiverSession != null && receiverSession.isOpen() && receiverRoom == chatMessageDto.getRoom_no()) {
			// receiverSession.sendMessage(new TextMessage(chatMessageDto.getMsg_content()));
			
			// 메시지 JSON 데이터 전달
			receiverSession.sendMessage(new TextMessage(message.getPayload()));
		}
		
		WebSocketSession senderSession = userSessions.get(chatMessageDto.getSender_no());
		Long senderRoom = userRooms.get(chatMessageDto.getSender_no());
		
		// 받는 사람이 존재하고, 받는 사람과의 소켓이 열려있으며, 방 번호가 일치할 때 - 쓴 사람도 내가 쓴 메시지를 볼 수 있어야되기 때문에 있어야함
		if(senderSession != null && senderSession.isOpen() && senderRoom == chatMessageDto.getRoom_no()) {
			senderSession.sendMessage(new TextMessage(message.getPayload()));
		}
		
		// ===============================================================================
		
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		// 연결이 끊겼을 때 동작
		// String userNo = session.getUri().getQuery().split("=")[1];		// ? 뒤에 있는 문자를 가져와서 = 기준으로 쪼개고 뒤에 있는 값
		// userSessions.remove(Long.parseLong(userNo));
		
		String userNo = getQueryParam(session, "senderNo");
		
		userSessions.remove(Long.parseLong(userNo));
		userRooms.remove(Long.parseLong(userNo));
	}
	
	/*
	 * author : Jayden
	 * history : 2025-03-24
	 * param :url, key data
	 * return : value data
	 * role(purpose) : WebSocketSession url parsing
	 */
	private String getQueryParam(WebSocketSession session, String key) {
		// senderNo=3&roomNo=1
		String query = session.getUri().getQuery();
		
		if(query != null) {
			String[] arr = query.split("&");
			// 0번 인덱스 : senderNo=3
			// 1번 인덱스 : roomNo=1
			
			for(String target : arr) {
				String[] keyArr = target.split("=");
				
				if(keyArr.length == 2 && keyArr[0].equals(key)) {
					return keyArr[1];
				}
			}
		}
		
		return null;
	}
	
}
