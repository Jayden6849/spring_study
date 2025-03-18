package com.gn.mvc.dto;

import com.gn.mvc.entity.Attach;
import com.gn.mvc.entity.Board;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class AttachDto {
	private Long attach_no;
	private Long board_no;
	private String ori_name;
	private String new_name;
	private String attach_path;
	
	public Attach toEntity() {
		return Attach.builder()
				.attachNo(attach_no)
				.board(Board.builder().boardNo(board_no).build())
				.oriName(ori_name)
				.newName(new_name)
				.attachPath(attach_path)
				.build();
	}
	
	public AttachDto toDto(Attach attach) {
		return AttachDto.builder()
				.attach_no(attach.getAttachNo())
				.board_no(attach.getBoard().getBoardNo())
				.ori_name(attach.getOriName())
				.new_name(attach.getNewName())
				.attach_path(attach.getAttachPath())
				.build();
		
	}
}
