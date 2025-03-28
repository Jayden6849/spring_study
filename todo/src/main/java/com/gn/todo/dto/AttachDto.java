package com.gn.todo.dto;

import com.gn.todo.entity.Attach;

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
public class AttachDto {
	
	private Long attach_no;
	private String ori_name;
	private String new_name;
	private String attach_path;
	
	public Attach toEntity() {
		return Attach.builder()
					.attachNo(this.attach_no)
					.oriName(this.ori_name)
					.newName(this.new_name)
					.attachPath(this.attach_path)
					.build();
	}
}
