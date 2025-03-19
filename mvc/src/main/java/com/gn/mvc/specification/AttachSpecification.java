package com.gn.mvc.specification;

import org.springframework.data.jpa.domain.Specification;

import com.gn.mvc.entity.Attach;
import com.gn.mvc.entity.Board;

public class AttachSpecification {
	
	// 매개받은 board와 데이터베이스의 board가 일치하는지 판단하는 조건
	public static Specification<Attach> boardEquals(Board board) {
		return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("board"), board);
	}
	
}
