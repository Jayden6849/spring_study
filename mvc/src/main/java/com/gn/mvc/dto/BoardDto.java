package com.gn.mvc.dto;

import java.time.LocalDateTime;

import com.gn.mvc.entity.Board;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Builder
public class BoardDto {
	
	private Long board_no;
	private String board_title;
	private String board_content;
	private LocalDateTime reg_date;
	private LocalDateTime mod_date;
	
	// 1. BoardDto -> Board(Entity) : DB 랑 소통할 떄는 Entity 이용함
	public Board toEntity() {
		
		return Board.builder()
				.boardNo(this.board_no)
				.boardTitle(this.board_title)
				.boardContent(this.board_content)
				.build();
		
	}
	
	// 2. Board(Entity) -> BoardDto : Java 랑 소통할 때는 DTO 이용함
	public BoardDto toDto(Board board) {
		
		return BoardDto.builder()
				.board_no(board.getBoardNo())
				.board_title(board.getBoardTitle())
				.board_content(board.getBoardContent())
				.reg_date(board.getRegDate())
				.mod_date(board.getModDate())
				.build();
						
	}
	
}
